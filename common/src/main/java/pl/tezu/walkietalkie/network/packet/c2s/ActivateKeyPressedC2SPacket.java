package pl.tezu.walkietalkie.network.packet.c2s;

import dev.architectury.networking.NetworkManager;
import pl.tezu.walkietalkie.Constants;
import pl.tezu.walkietalkie.ModSoundEvents;
import pl.tezu.walkietalkie.Util;
import pl.tezu.walkietalkie.item.WalkieTalkieItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public record ActivateKeyPressedC2SPacket(float volume) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ActivateKeyPressedC2SPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "activate_keypressed_c2s"));
    public static final StreamCodec<ByteBuf, ActivateKeyPressedC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ActivateKeyPressedC2SPacket::volume,
            ActivateKeyPressedC2SPacket::new);

    public static void receive(ActivateKeyPressedC2SPacket packet, NetworkManager.PacketContext packetContext) {
        ServerPlayer player = (ServerPlayer) packetContext.getPlayer();

        ItemStack stack = Util.getWalkieTalkieInHand(player);

        if (stack == null)
            stack = Util.getOptimalWalkieTalkieRange(player);
        if (stack == null) {
            return;
        }

        boolean activated = WalkieTalkieItem.isActivate(stack);

        WalkieTalkieItem.setActivate(stack, !activated);

        float vol = Mth.clamp(packet.volume(), 0.0f, 1.0f);
        Holder<SoundEvent> holder = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(activated ? ModSoundEvents.OFF_SOUND_EVENT.get() : ModSoundEvents.ON_SOUND_EVENT.get());
        player.connection.send(new ClientboundSoundPacket(holder, SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), vol, 1f, 0));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
