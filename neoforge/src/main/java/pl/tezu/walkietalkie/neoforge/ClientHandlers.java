package pl.tezu.walkietalkie.neoforge;

import pl.tezu.walkietalkie.client.gui.screen.SpeakerScreen;
import pl.tezu.walkietalkie.client.gui.screen.WalkieTalkieScreen;
import pl.tezu.walkietalkie.network.packet.s2c.UpdateWalkieTalkieS2CPacket;
import pl.tezu.walkietalkie.screen.ModScreenHandlers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Holds client-only event handlers and packet handlers so that client classes
 * (Screen, Minecraft, etc.) are never referenced or loaded on a dedicated server.
 */
@OnlyIn(Dist.CLIENT)
public class ClientHandlers {
    private ClientHandlers() {
    }

    public static void registerConfigScreen(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                net.neoforged.neoforge.client.gui.ConfigurationScreen::new);
    }

    public static void handleUpdateWalkieTalkie(UpdateWalkieTalkieS2CPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> WalkieTalkieScreen.getInstance().updateButtons(packet.stack()));
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModScreenHandlers.SPEAKER.get(), SpeakerScreen::new);
    }
}
