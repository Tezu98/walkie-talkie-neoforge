package pl.tezu.walkietalkie.network.packet.c2s;

import dev.architectury.networking.NetworkManager;
import pl.tezu.walkietalkie.Constants;
import pl.tezu.walkietalkie.radio.TransmitFromHandManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record TransmitFromHandC2SPacket(boolean enabled) implements CustomPacketPayload {

    public static final Type<TransmitFromHandC2SPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "transmit_from_hand_c2s"));
    public static final StreamCodec<ByteBuf, TransmitFromHandC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, TransmitFromHandC2SPacket::enabled, TransmitFromHandC2SPacket::new);

    public static void receive(TransmitFromHandC2SPacket packet, NetworkManager.PacketContext context) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        TransmitFromHandManager.setEnabled(player.getUUID(), packet.enabled());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

