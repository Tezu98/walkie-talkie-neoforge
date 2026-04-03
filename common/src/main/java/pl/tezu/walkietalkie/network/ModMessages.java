package pl.tezu.walkietalkie.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import pl.tezu.walkietalkie.network.packet.s2c.UpdateWalkieTalkieS2CPacket;

import java.util.function.Consumer;

public class ModMessages {

    /**
     * Platform-specific hook for sending C2S packets.
     * On NeoForge this is set to {@code PacketDistributor::sendToServer} by
     * {@code WalkieTalkieNeoForge} (client-side only) so that packets bypass
     * Architectury's {@code NetworkAggregator} and use NeoForge's native send
     * path, which respects the {@code optional()} flag registered via
     * {@code RegisterPayloadHandlersEvent} and therefore passes
     * {@code NetworkRegistry.checkPacket} even when Sinytra Connector is present.
     *
     * <p>Default is {@code null} — must NOT use {@code NetworkManager::sendToServer}
     * here because that method is {@code @OnlyIn(Dist.CLIENT)} and would be stripped
     * by NeoForge's {@code runtimedistcleaner} on the server, causing a
     * {@code NoSuchMethodError} when the class is loaded during server startup.
     */
    public static Consumer<CustomPacketPayload> c2sSender = null;

    /**
     * Send a C2S packet using the platform-appropriate sender.
     */
    public static void sendToServer(CustomPacketPayload payload) {
        c2sSender.accept(payload);
    }

    public static void registerS2CPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UpdateWalkieTalkieS2CPacket.TYPE, UpdateWalkieTalkieS2CPacket.STREAM_CODEC, UpdateWalkieTalkieS2CPacket::receive);
    }

}
