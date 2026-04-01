package fr.flaton.walkietalkie.radio;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PushToTalkManager {

    private static final Set<UUID> PRESSED = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void setPressed(UUID uuid, boolean pressed) {
        if (pressed) {
            PRESSED.add(uuid);
        } else {
            PRESSED.remove(uuid);
        }
    }

    public static boolean isPressed(UUID uuid) {
        return PRESSED.contains(uuid);
    }
}

