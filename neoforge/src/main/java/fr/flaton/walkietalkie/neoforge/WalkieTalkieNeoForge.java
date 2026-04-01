package fr.flaton.walkietalkie.neoforge;

import fr.flaton.walkietalkie.Constants;
import fr.flaton.walkietalkie.WalkieTalkie;
import fr.flaton.walkietalkie.WalkieTalkieClient;
import fr.flaton.walkietalkie.config.ModConfig;
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
