package pl.tezu.walkietalkie.radio;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import pl.tezu.walkietalkie.Util;
import pl.tezu.walkietalkie.block.entity.SpeakerBlockEntity;
import pl.tezu.walkietalkie.config.ModConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

import static pl.tezu.walkietalkie.WalkieTalkieVoiceChatPlugin.voiceChatAPI;

public class Member {

    private final static Map<UUID, Member> MEMBERS = new HashMap<>();

    /**
     * Platform-specific hook for injecting contraption-borne speakers (e.g. Create integration).
     * Set by the NeoForge module on startup when Create is present; {@code null} otherwise.
     */
    public static BiConsumer<MinecraftServer, List<Member>> createIntegrationHook = null;

    private final UUID uuid;
    private Vec3 pos;
    private Level level;
    private final Set<Canal> canals = new HashSet<>();

    private short volume = 0;

    /** One Opus decoder per sending-player UUID, so interleaved streams are decoded independently. */
    private final Map<UUID, OpusDecoder> decoders = new HashMap<>();
    private final List<short[]> packetBuffer = new ArrayList<>();
    /**
     * Guarded by {@link #audioLock} for creation; volatile so the null-write in
     * {@link #getAudio()} is immediately visible to other threads without a lock.
     */
    private volatile AudioPlayer audioPlayer;
    private volatile AudioChannel audioChannel;
    private final Object audioLock = new Object();

    public static void serverTick(MinecraftServer server) {
        List<ServerPlayer> playerList = server.getPlayerList().getPlayers();
        List<Member> members = new ArrayList<>();

        for (ServerPlayer player : playerList) {
            Member member = Member.get(player.getUUID(), player.position(), player.level(), Util.getCanals(player));
            if (member != null)
                members.add(member);
        }
        for (SpeakerBlockEntity speaker : SpeakerBlockEntity.getActiveSpeakers()) {
            Member member = Member.get(speaker.getUuid(), speaker.getBlockPos().getCenter(), speaker.getLevel(), speaker.getCanal());
            if (member != null)
                members.add(member);
        }

        // Create mod integration: also track speakers that are inside a moving contraption.
        // Those speakers never appear in SpeakerBlockEntity.SPEAKERS because Create stores
        // their block entities as raw NBT (no live Java object is constructed).
        if (createIntegrationHook != null) {
            createIntegrationHook.accept(server, members);
        }

        for (Member member : new HashSet<>(MEMBERS.values())) {
            if (!members.contains(member)) {
                for (Canal canal : member.canals) {
                    canal.removeMember(member);
                }
                MEMBERS.remove(member.uuid);
                if (member.audioPlayer != null) {
                    member.audioPlayer.stopPlaying();
                }
                for (OpusDecoder d : member.decoders.values()) {
                    d.close();
                }
                member.decoders.clear();
            }
        }
    }

    public static Member get(UUID uuid) {
        return MEMBERS.get(uuid);
    }

    /**
     * Creates or updates a {@link Member} for an entity that is not a player and not a
     * placed {@link pl.tezu.walkietalkie.block.entity.SpeakerBlockEntity} — used by the
     * Create contraption integration to inject contraption-borne speakers.
     */
    public static @Nullable Member getOrCreate(UUID uuid, Vec3 pos, Level level, Set<Canal> canals) {
        return get(uuid, pos, level, canals);
    }

    private static @Nullable Member get(UUID uuid, Vec3 pos, Level level, Set<Canal> canals) {
        if (canals.isEmpty()) return null;
        Member member = MEMBERS.computeIfAbsent(uuid, Member::new);
        member.update(pos, level, canals);
        return member;
    }

    private Member(UUID uuid) {
        this.uuid = uuid;
    }

    private void update(Vec3 pos, Level level, Set<Canal> canals) {
        this.pos = pos;
        this.level = level;

        for (Canal canal : canals) {
            if (!this.canals.contains(canal)) {
                canal.addMember(this);
                this.canals.add(canal);
            }
        }

        for (Canal canal : new HashSet<>(this.canals)) {
            if (!canals.contains(canal)) {
                canal.removeMember(this);
                this.canals.remove(canal);
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public Vec3 getPos() {
        return pos;
    }

    public Level getLevel() {
        return level;
    }

    public Set<Canal> getCanals() {
        return canals;
    }

    public void sendAudio(UUID senderUuid, MicrophonePacket packet) {
        // Each sender gets its own Opus decoder so interleaved streams stay independent.
        OpusDecoder senderDecoder = decoders.computeIfAbsent(senderUuid, id -> voiceChatAPI.createDecoder());
        byte[] data = packet.getOpusEncodedData();
        short[] decoded = senderDecoder.decode(data);
        synchronized (packetBuffer) {
            packetBuffer.add(decoded);
        }

        // Store a local reference to avoid a race where the audio callback nulls
        // audioPlayer between the null-check and the startPlaying() call.
        AudioPlayer player = getAudioPlayer();
        if (player != null) {
            player.startPlaying();
        }
    }

    private AudioPlayer getAudioPlayer() {
        // Fast path – already created.
        if (audioPlayer != null) return audioPlayer;

        synchronized (audioLock) {
            // Double-checked: another thread may have created it while we waited.
            if (audioPlayer != null) return audioPlayer;

            // Use a fresh UUID for every new channel so the API never sees the same ID
            // reused while a previous channel may still be in flight after stopPlaying().
            UUID channelId = UUID.randomUUID();
            VoicechatConnection connection = voiceChatAPI.getConnectionOf(uuid);

            if (connection == null) {
                // ── Speaker ──────────────────────────────────────────────────────────
                LocationalAudioChannel locationalAudioChannel = voiceChatAPI.createLocationalAudioChannel(channelId,
                        voiceChatAPI.fromServerLevel(level),
                        voiceChatAPI.createPosition(pos.x, pos.y, pos.z));
                if (locationalAudioChannel == null) return null;
                locationalAudioChannel.setDistance(ModConfig.speakerDistance);
                audioChannel = locationalAudioChannel;
                audioChannel.setCategory("speakers");
            } else {
                // ── Player ───────────────────────────────────────────────────────────
                // Use StaticAudioChannel instead of EntityAudioChannel.
                // EntityAudioChannel internally loads EntityAudioChannelImpl, which
                // triggers the Railways EntityAudioChannelImplMixin.  That mixin has a
                // type-signature bug (handler declares Object, target expects Entity)
                // that raises a fatal MixinTransformerError and crashes the game.
                // StaticAudioChannel uses StaticAudioChannelImpl — a completely
                // different class that is not targeted by any Railways mixin.
                StaticAudioChannel staticAudioChannel = voiceChatAPI.createStaticAudioChannel(channelId);
                if (staticAudioChannel == null) return null;
                staticAudioChannel.addTarget(connection);
                audioChannel = staticAudioChannel;
            }

            // Volatile write – makes the new player visible to all threads without
            // requiring them to enter the synchronized block.
            audioPlayer = voiceChatAPI.createAudioPlayer(audioChannel, voiceChatAPI.createEncoder(), this::getAudio);
            return audioPlayer;
        }
    }

    private short[] getAudio() {
        short[] audio = getCombinedAudio();
        if (audio == null) {
            volume = 0;
            AudioPlayer player = audioPlayer;
            audioPlayer = null;
            if (player != null) player.stopPlaying();
            return null;
        }
        volume = getVolume(audio);
        return audio;
    }

    private short getVolume(short[] audio) {
        short max = 0;
        for (short num : audio) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

    public short[] getCombinedAudio() {
        List<short[]> snapshot;
        synchronized (packetBuffer) {
            if (packetBuffer.isEmpty()) {
                return null;
            }
            snapshot = new ArrayList<>(packetBuffer);
            packetBuffer.clear();
        }

        short[] result = new short[960];
        for (int i = 0; i < result.length; i++) {
            int sample = 0;
            for (short[] audio : snapshot) {
                sample += audio[i];
            }
            result[i] = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, sample));
        }
        return result;
    }

    public boolean isListening() {
        return volume != 0;
    }

    public short getVolume() {
        return volume;
    }
}