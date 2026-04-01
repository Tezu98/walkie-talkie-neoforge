package pl.tezu.walkietalkie.neoforge;

import pl.tezu.walkietalkie.Constants;
import pl.tezu.walkietalkie.CuriosLoader;
import pl.tezu.walkietalkie.WalkieTalkie;
import pl.tezu.walkietalkie.WalkieTalkieClient;
import pl.tezu.walkietalkie.neoforge.datagen.WalkieTalkieCuriosProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(Constants.MOD_ID)
public class WalkieTalkieNeoForge {
    public WalkieTalkieNeoForge(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, NeoForgeConfig.SPEC, "WalkieTalkie.toml");

        // Set the save hook so GUI changes (transmitFromHand, effectVolume) persist to the TOML file
        pl.tezu.walkietalkie.config.ModConfig.saveHook = NeoForgeConfig::saveFromModConfig;

        modBus.addListener(this::onConfigLoad);
        modBus.addListener(this::onConfigReload);
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::gatherData);

        WalkieTalkie.init();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            WalkieTalkieClient.init();
            modBus.addListener(ClientHandlers::registerScreens);
            ClientHandlers.registerConfigScreen(modContainer);
        }
    }

    private void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new WalkieTalkieCuriosProvider(
                        Constants.MOD_ID,
                        event.getGenerator().getPackOutput(),
                        event.getExistingFileHelper(),
                        event.getLookupProvider()
                )
        );
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("curios")) {
            CuriosLoader.register();
        }
    }

    private void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == NeoForgeConfig.SPEC) {
            NeoForgeConfig.syncToModConfig();
        }
    }

    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == NeoForgeConfig.SPEC) {
            NeoForgeConfig.syncToModConfig();
        }
    }
}
