package fr.flaton.walkietalkie.neoforge;

import fr.flaton.walkietalkie.client.gui.screen.SpeakerScreen;
import fr.flaton.walkietalkie.client.gui.screen.WalkieTalkieScreen;
import fr.flaton.walkietalkie.network.packet.s2c.UpdateWalkieTalkieS2CPacket;
import fr.flaton.walkietalkie.screen.ModScreenHandlers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Holds client-only event handlers and packet handlers so that client classes
 * (Screen, Minecraft, etc.) are never referenced or loaded on a dedicated server.
 */
@OnlyIn(Dist.CLIENT)
public class ClientHandlers {

    public static void handleUpdateWalkieTalkie(UpdateWalkieTalkieS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> WalkieTalkieScreen.getInstance().updateButtons(packet.stack()));
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModScreenHandlers.SPEAKER.get(), SpeakerScreen::new);
    }
}
