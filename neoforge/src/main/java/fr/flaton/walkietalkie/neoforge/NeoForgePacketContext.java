package fr.flaton.walkietalkie.neoforge;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Adapts NeoForge's IPayloadContext to Architectury's NetworkManager.PacketContext,
 * allowing shared packet receive methods to work with NeoForge's native registration.
 */
public class NeoForgePacketContext implements NetworkManager.PacketContext {

    private final IPayloadContext context;

    public NeoForgePacketContext(IPayloadContext context) {
        this.context = context;
    }

    @Override
    public Player getPlayer() {
        return context.player();
    }

    @Override
    public void queue(Runnable runnable) {
        context.enqueueWork(runnable);
    }

    @Override
    public Env getEnvironment() {
        return context.flow() == PacketFlow.SERVERBOUND ? Env.SERVER : Env.CLIENT;
    }

    @Override
    public RegistryAccess registryAccess() {
        return context.player().level().registryAccess();
    }
}
