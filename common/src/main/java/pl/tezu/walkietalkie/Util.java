package pl.tezu.walkietalkie;

import pl.tezu.walkietalkie.config.ModConfig;
import pl.tezu.walkietalkie.item.WalkieTalkieItem;
import pl.tezu.walkietalkie.radio.Canal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Util {

    /**
     * Injected by CuriosLoader.register() when Curios is present.
     * Kept as a plain Function so Util never references any Curios type.
     */
    static Function<Player, List<ItemStack>> curioWalkieTalkieSupplier = null;

    /**
     * Returns walkie-talkies equipped in the Curios walkie_talkie slot.
     * Returns an empty list when Curios is absent.
     */
    public static List<ItemStack> getCurioWalkieTalkies(Player player) {
        if (curioWalkieTalkieSupplier != null) {
            return curioWalkieTalkieSupplier.apply(player);
        }
        return List.of();
    }

    public static @Nullable ItemStack getWalkieTalkieInHand(@NotNull Player player) {

        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        if (mainHand.getItem() instanceof WalkieTalkieItem) {
            return mainHand;
        }
        if (offHand.getItem() instanceof WalkieTalkieItem) {
            return offHand;
        }
        return null;
    }

    public static boolean canBroadcastToReceiver(Level senderLevel, Level receiverLevel, Vec3 senderPos, Vec3 receiverPos, int range) {
        if (!ModConfig.crossDimensionsEnabled && !receiverLevel.dimension().equals(senderLevel.dimension()))
            return false;

        double senderCoordinateScale = senderLevel.dimensionType().coordinateScale();
        double receiverCoordinateScale = receiverLevel.dimensionType().coordinateScale();

        double appliedRange = ModConfig.applyDimensionScale ? range / Math.max(senderCoordinateScale, receiverCoordinateScale) : range;

        return senderPos.closerThan(receiverPos, appliedRange);
    }

    public static List<ItemStack> getWalkieTalkies(Player player) {

        List<ItemStack> itemStacks = new ArrayList<>();

        Inventory playerInventory = player.getInventory();
        List<ItemStack> inventory = new ArrayList<>(playerInventory.items); // all 36 slots
        inventory.addAll(playerInventory.offhand);

        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.getItem() instanceof WalkieTalkieItem) {
                itemStacks.add(stack);
            }

        }

        itemStacks.addAll(getCurioWalkieTalkies(player));

        return itemStacks;
    }

    public static @Nullable ItemStack getOptimalWalkieTalkieRange(Player player) {
        List<ItemStack> itemStacks = getWalkieTalkies(player);
        if (itemStacks.isEmpty()) {
            return null;
        }

        ItemStack itemStack = null;
        int range = 0;

        for (ItemStack stack : itemStacks) {

            int rng = WalkieTalkieItem.getRange(stack);

            if (rng > range) {
                itemStack = stack;
                range = rng;
            }
        }

        return itemStack;
    }

    public static @Nullable ItemStack getWalkieTalkieActivated(Player player) {
        ItemStack stack = getOptimalWalkieTalkieRange(player);
        if (stack != null && WalkieTalkieItem.isActivate(stack)) {
            return stack;
        }
        return null;
    }

    public static List<ItemStack> getActivatedWalkieTalkies(ServerPlayer player) {
        List<ItemStack> walkieTalkies = getWalkieTalkiesForListening(player);
        walkieTalkies.removeIf(walkieTalkie -> !WalkieTalkieItem.isActivate(walkieTalkie));
        return walkieTalkies;
    }

    /**
     * Returns walkie-talkies that count for listening, respecting the
     * {@code require-hotbar-for-listening} server config option.
     * When {@code true}, only hotbar slots (0-8) and the offhand are checked.
     * When {@code false} (default), the entire inventory is checked.
     */
    public static List<ItemStack> getWalkieTalkiesForListening(Player player) {
        List<ItemStack> itemStacks = new ArrayList<>();
        Inventory playerInventory = player.getInventory();

        List<ItemStack> slots;
        if (ModConfig.requireHotbarForListening) {
            // Hotbar is slots 0-8 in playerInventory.items
            slots = new ArrayList<>(playerInventory.items.subList(0, 9));
            slots.addAll(playerInventory.offhand);
        } else {
            slots = new ArrayList<>(playerInventory.items);
            slots.addAll(playerInventory.offhand);
        }

        for (ItemStack stack : slots) {
            if (!stack.isEmpty() && stack.getItem() instanceof WalkieTalkieItem) {
                itemStacks.add(stack);
            }
        }

        // Curio slot always counts for listening regardless of requireHotbarForListening
        itemStacks.addAll(getCurioWalkieTalkies(player));

        return itemStacks;
    }

    public static Set<Canal> getCanals(ServerPlayer player) {
        Set<Canal> canals = new HashSet<>();
        for (ItemStack itemStack : getActivatedWalkieTalkies(player)) {
            canals.add(Canal.getOrCreate(WalkieTalkieItem.getCanal(itemStack)));
        }
        return canals;
    }

    public static int loop(int value, int min, int max) {

        if (value > max) {
            value = min;
        } else if (value < min) {
            value = max;
        }
        return value;

    }
}
