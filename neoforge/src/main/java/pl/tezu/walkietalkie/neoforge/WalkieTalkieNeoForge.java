package pl.tezu.walkietalkie.neoforge;

import pl.tezu.walkietalkie.Constants;
import pl.tezu.walkietalkie.WalkieTalkie;
import pl.tezu.walkietalkie.WalkieTalkieClient;
import pl.tezu.walkietalkie.config.ModConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

@Mod(Constants.MOD_ID)
public class WalkieTalkieNeoForge {
    public WalkieTalkieNeoForge(IEventBus modBus) {
        ModConfig config = new ModConfig(FMLPaths.CONFIGDIR.get());
        config.loadModConfig();

        WalkieTalkie.init();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            WalkieTalkieClient.init();
            modBus.addListener(ClientHandlers::registerScreens);
        }
    }
}
