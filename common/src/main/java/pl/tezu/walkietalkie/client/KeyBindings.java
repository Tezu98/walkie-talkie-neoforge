package pl.tezu.walkietalkie.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import pl.tezu.walkietalkie.ModSoundEvents;
import pl.tezu.walkietalkie.Util;
import pl.tezu.walkietalkie.config.ModConfig;
import pl.tezu.walkietalkie.network.ModMessages;
import pl.tezu.walkietalkie.network.packet.c2s.ActivateKeyPressedC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.PushToTalkC2SPacket;
import net.minecraft.client.KeyMapping;

public class KeyBindings {

    public static final KeyMapping ACTIVATE = new KeyMapping(
            "key.walkietalkie.activate",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_B,
            "category.walkietalkie.keys"
    );

    public static final KeyMapping PUSH_TO_TALK = new KeyMapping(
            "key.walkietalkie.push_to_talk",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "category.walkietalkie.keys"
    );

    private static boolean pttWasDown = false;

    public static void register() {
        KeyMappingRegistry.register(ACTIVATE);
        KeyMappingRegistry.register(PUSH_TO_TALK);

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (ACTIVATE.consumeClick()) {
                ModMessages.sendToServer(new ActivateKeyPressedC2SPacket(ModConfig.effectVolume));
            }

            boolean pttDown = PUSH_TO_TALK.isDown();
            boolean hasActivatedWT = minecraft.player != null && Util.getWalkieTalkieActivated(minecraft.player) != null;

            if (!hasActivatedWT) {
                // If the walkie talkie is off and PTT was considered held, release it server-side
                if (pttWasDown) {
                    ModMessages.sendToServer(new PushToTalkC2SPacket(false));
                    pttWasDown = false;
                }
            } else if (pttDown != pttWasDown) {
                ModMessages.sendToServer(new PushToTalkC2SPacket(pttDown));
                if (minecraft.player != null) {
                    minecraft.player.playSound(
                        pttDown ? ModSoundEvents.ON_SOUND_EVENT.get() : ModSoundEvents.OFF_SOUND_EVENT.get(),
                        ModConfig.effectVolume, 1.0f);
                }
                pttWasDown = pttDown;
            }
        });
    }

}
