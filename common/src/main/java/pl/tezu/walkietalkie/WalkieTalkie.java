package pl.tezu.walkietalkie;

import dev.architectury.event.events.common.TickEvent;
import pl.tezu.walkietalkie.block.ModBlocks;
import pl.tezu.walkietalkie.block.entity.ModBlockEntities;
import pl.tezu.walkietalkie.item.ModDataComponents;
import pl.tezu.walkietalkie.item.ModItemGroup;
import pl.tezu.walkietalkie.item.ModItems;
import pl.tezu.walkietalkie.network.ModMessages;
import pl.tezu.walkietalkie.radio.SoundManager;
import pl.tezu.walkietalkie.screen.ModScreenHandlers;

public class WalkieTalkie {

	public static void init() {
		ModBlocks.register();
		ModItems.register();
		ModDataComponents.register();
		ModItemGroup.register();

		ModBlockEntities.register();
		ModScreenHandlers.register();

		ModMessages.registerC2SPackets();
		ModMessages.registerS2CPackets();

		ModSoundEvents.register();
		TickEvent.SERVER_POST.register(SoundManager::serverTick);
	}
}