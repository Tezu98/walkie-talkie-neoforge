package pl.tezu.walkietalkie.neoforge.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import pl.tezu.walkietalkie.Constants;

public class WalkieTalkieCuriosProvider extends CuriosDataProvider {

    public WalkieTalkieCuriosProvider(String modId, PackOutput output,
                                      ExistingFileHelper fileHelper,
                                      CompletableFuture<HolderLookup.Provider> registries) {
        super(modId, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        this.createSlot("walkie_talkie")
                .size(1)
                .icon(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "slot/empty_walkie_talkie_slot"))
                .addCosmetic(false);

        this.createEntities("walkietalkie")
                .addPlayer()
                .addSlots("walkie_talkie");
    }
}
