package pl.tezu.walkietalkie;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import pl.tezu.walkietalkie.item.WalkieTalkieItem;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Isolated class that holds all direct references to Curios API types.
 * It must ONLY be loaded when Curios is present on the classpath.
 * It injects a plain Function into Util so Util never references this class directly.
 */
public class CuriosLoader {

    public static void register() {
        // Inject the supplier — Util holds a plain Function, no Curios type leaks
        Util.curioWalkieTalkieSupplier = CuriosLoader::getCurioWalkieTalkies;

        WalkieTalkieCurioItem curioItem = new WalkieTalkieCurioItem();
        ModCurioItems.CURIOS.forEach(supplier -> CuriosApi.registerCurio(supplier.get(), curioItem));
    }

    private static List<ItemStack> getCurioWalkieTalkies(Player player) {
        List<ItemStack> result = new ArrayList<>();
        CuriosApi.getCuriosInventory(player).ifPresent(handler ->
            handler.findCurios(stack -> stack.getItem() instanceof WalkieTalkieItem)
                   .forEach(slotResult -> result.add(slotResult.stack()))
        );
        return result;
    }
}
