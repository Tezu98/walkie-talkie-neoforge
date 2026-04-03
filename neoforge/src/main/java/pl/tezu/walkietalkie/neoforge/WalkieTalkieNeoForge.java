package pl.tezu.walkietalkie.neoforge;

import pl.tezu.walkietalkie.Constants;
import pl.tezu.walkietalkie.CuriosLoader;
import pl.tezu.walkietalkie.WalkieTalkie;
import pl.tezu.walkietalkie.WalkieTalkieClient;
import pl.tezu.walkietalkie.network.ModMessages;
import pl.tezu.walkietalkie.network.packet.c2s.ActivateKeyPressedC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.PushToTalkC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.TransmitFromHandC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.speaker.ButtonSpeakerC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.speaker.CanalSpeakerC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.walkietalkie.ButtonWalkieTalkieC2SPacket;
import pl.tezu.walkietalkie.network.packet.c2s.walkietalkie.CanalWalkieTalkieC2SPacket;
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
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Constants.MOD_ID)
public class WalkieTalkieNeoForge {
    public WalkieTalkieNeoForge(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, NeoForgeConfig.SPEC, "WalkieTalkie.toml");

        // Set the save hook so GUI changes (transmitFromHand, effectVolume) persist to the TOML file
        pl.tezu.walkietalkie.config.ModConfig.saveHook = NeoForgeConfig::saveFromModConfig;

        // Route all C2S packet sends through NeoForge's PacketDistributor so they use the
        // optional() registration from RegisterPayloadHandlersEvent, bypassing both
        // Architectury's NetworkAggregator (avoids NPE when registerC2SPackets is not called)
        // and NeoForge's checkPacket handshake validation (avoids UnsupportedOperationException
        // with Sinytra Connector's NetworkRegistryMixin).
        if (FMLLoader.getDist() == Dist.CLIENT) {
            ModMessages.c2sSender = PacketDistributor::sendToServer;
        }

        modBus.addListener(this::onConfigLoad);
        modBus.addListener(this::onConfigReload);
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::gatherData);
        modBus.addListener(this::registerPayloadHandlers);

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

    /**
     * Registers all C2S network payloads using NeoForge's native API with the
     * {@code optional()} flag. This bypasses NeoForge's {@code checkPacket}
     * handshake validation, which Sinytra Connector's NetworkRegistryMixin can
     * break, causing an {@code UnsupportedOperationException} when right-clicking
     * the walkie-talkie on a multiplayer server.
     *
     * <p>The S2C payload ({@code UpdateWalkieTalkieS2CPacket}) is still registered
     * via Architectury's {@code NetworkManager.registerReceiver} in
     * {@code ModMessages.registerS2CPackets()} and is not duplicated here.
     */
    private void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Constants.MOD_ID).optional();

        registrar.playToServer(
                ButtonWalkieTalkieC2SPacket.TYPE,
                ButtonWalkieTalkieC2SPacket.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> ButtonWalkieTalkieC2SPacket.receive(p, new NeoForgePacketContext(ctx))));

        registrar.playToServer(
                CanalWalkieTalkieC2SPacket.TYPE,
                CanalWalkieTalkieC2SPacket.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> CanalWalkieTalkieC2SPacket.receive(p, new NeoForgePacketContext(ctx))));

        registrar.playToServer(
                ButtonSpeakerC2SPacket.TYPE,
                ButtonSpeakerC2SPacket.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> ButtonSpeakerC2SPacket.receive(p, new NeoForgePacketContext(ctx))));

        registrar.playToServer(
                CanalSpeakerC2SPacket.TYPE,
                CanalSpeakerC2SPacket.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> CanalSpeakerC2SPacket.receive(p, new NeoForgePacketContext(ctx))));

        registrar.playToServer(
                ActivateKeyPressedC2SPacket.TYPE,
                ActivateKeyPressedC2SPacket.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> ActivateKeyPressedC2SPacket.receive(p, new NeoForgePacketContext(ctx))));

        registrar.playToServer(
                PushToTalkC2SPacket.TYPE,
                PushToTalkC2SPacket.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> PushToTalkC2SPacket.receive(p, new NeoForgePacketContext(ctx))));

        registrar.playToServer(
                TransmitFromHandC2SPacket.TYPE,
                TransmitFromHandC2SPacket.STREAM_CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> TransmitFromHandC2SPacket.receive(p, new NeoForgePacketContext(ctx))));
    }
}
