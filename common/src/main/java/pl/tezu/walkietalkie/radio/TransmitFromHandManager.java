package pl.tezu.walkietalkie.radio;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which players have "transmit from hand" enabled.
 * When a player's UUID is present in this set, the walkie-talkie will
 * automatically transmit when held in main hand or off-hand (the default behaviour).
 * When absent, the item must be activated via Push-to-Talk or another explicit action.
 *
 * New players default to enabled (set is populated on first toggle / login is not required;
 * the packet handler inserts/removes as needed, and the check defaults to true when absent).
 */
public class TransmitFromHandManager {

    /** UUIDs of players who have transmit-from-hand DISABLED. */
    private static final Set<UUID> DISABLED = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void setEnabled(UUID uuid, boolean enabled) {
        if (enabled) {
            DISABLED.remove(uuid);
        } else {
            DISABLED.add(uuid);
        }
    }

    /** Returns true (the default) unless the player has explicitly disabled it. */
    public static boolean isEnabled(UUID uuid) {
        return !DISABLED.contains(uuid);
    }
}

