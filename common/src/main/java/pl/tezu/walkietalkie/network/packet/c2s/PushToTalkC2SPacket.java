package pl.tezu.walkietalkie.network.packet.c2s;

import dev.architectury.networking.NetworkManager;
import pl.tezu.walkietalkie.Constants;
import pl.tezu.walkietalkie.radio.PushToTalkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record PushToTalkC2SPacket(boolean pressed) implements CustomPacketPayload {

    public static final Type<PushToTalkC2SPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "push_to_talk_c2s"));
    public static final StreamCodec<ByteBuf, PushToTalkC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PushToTalkC2SPacket::pressed, PushToTalkC2SPacket::new);

    public static void receive(PushToTalkC2SPacket packet, NetworkManager.PacketContext context) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        if (packet.pressed()) {
            PushToTalkManager.setPressed(player.getUUID(), true);
        } else {
            PushToTalkManager.setPressed(player.getUUID(), false);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

