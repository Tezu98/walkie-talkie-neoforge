package pl.tezu.walkietalkie.network;

import dev.architectury.networking.NetworkManager;
import pl.tezu.walkietalkie.network.packet.c2s.ActivateKeyPressedC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.PushToTalkC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.TransmitFromHandC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.speaker.ButtonSpeakerC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.speaker.CanalSpeakerC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.walkietalkie.ButtonWalkieTalkieC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.walkietalkie.CanalWalkieTalkieC2SPacket;
import pl.tezu.walkietalkie.network.packet.s2c.UpdateWalkieTalkieS2CPacket;

public class ModMessages {

    public static void registerC2SPackets() {

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ButtonWalkieTalkieC2SPacket.TYPE, ButtonWalkieTalkieC2SPacket.STREAM_CODEC, ButtonWalkieTalkieC2SPacket::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CanalWalkieTalkieC2SPacket.TYPE, CanalWalkieTalkieC2SPacket.STREAM_CODEC, CanalWalkieTalkieC2SPacket::receive);

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ButtonSpeakerC2SPacket.TYPE, ButtonSpeakerC2SPacket.STREAM_CODEC, ButtonSpeakerC2SPacket::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CanalSpeakerC2SPacket.TYPE, CanalSpeakerC2SPacket.STREAM_CODEC, CanalSpeakerC2SPacket::receive);

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ActivateKeyPressedC2SPacket.TYPE, ActivateKeyPressedC2SPacket.STREAM_CODEC, ActivateKeyPressedC2SPacket::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, PushToTalkC2SPacket.TYPE, PushToTalkC2SPacket.STREAM_CODEC, PushToTalkC2SPacket::receive);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, TransmitFromHandC2SPacket.TYPE, TransmitFromHandC2SPacket.STREAM_CODEC, TransmitFromHandC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UpdateWalkieTalkieS2CPacket.TYPE, UpdateWalkieTalkieS2CPacket.STREAM_CODEC, UpdateWalkieTalkieS2CPacket::receive);
    }

}
