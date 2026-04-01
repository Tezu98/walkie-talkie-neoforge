package pl.tezu.walkietalkie.block.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import pl.tezu.walkietalkie.Constants;
import pl.tezu.walkietalkie.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;


public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Constants.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<SpeakerBlockEntity>> SPEAKER = BLOCK_ENTITIES.register("speaker", () ->
            BlockEntityType.Builder.of(SpeakerBlockEntity::new, ModBlocks.SPEAKER.get()).build(null));

    public static void register() {
        BLOCK_ENTITIES.register();
    }



}
