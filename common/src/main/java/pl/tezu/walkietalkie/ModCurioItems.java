package pl.tezu.walkietalkie;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import pl.tezu.walkietalkie.item.ModItems;

import java.util.ArrayList;
import java.util.List;

public class ModCurioItems {

    static final List<RegistrySupplier<? extends Item>> CURIOS = new ArrayList<>();

    public static void add(RegistrySupplier<? extends Item> item) {
        CURIOS.add(item);
    }

    static {
        add(ModItems.WOODEN_WALKIETALKIE);
        add(ModItems.STONE_WALKIETALKIE);
        add(ModItems.IRON_WALKIETALKIE);
        add(ModItems.GOLDEN_WALKIETALKIE);
        add(ModItems.DIAMOND_WALKIETALKIE);
        add(ModItems.NETHERITE_WALKIETALKIE);
    }
}
