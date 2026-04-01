package pl.tezu.walkietalkie.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;
import pl.tezu.walkietalkie.config.ModConfig;

public class NeoForgeConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // Channel settings
    public static final ModConfigSpec.IntValue MAX_CANAL;

    // Speaker settings
    public static final ModConfigSpec.IntValue SPEAKER_DISTANCE;
    public static final ModConfigSpec.BooleanValue VOICE_DUPLICATION;

    // Walkie-Talkie range settings
    public static final ModConfigSpec.IntValue WOODEN_WALKIETALKIE_RANGE;
    public static final ModConfigSpec.IntValue STONE_WALKIETALKIE_RANGE;
    public static final ModConfigSpec.IntValue IRON_WALKIETALKIE_RANGE;
    public static final ModConfigSpec.IntValue GOLDEN_WALKIETALKIE_RANGE;
    public static final ModConfigSpec.IntValue DIAMOND_WALKIETALKIE_RANGE;
    public static final ModConfigSpec.IntValue NETHERITE_WALKIETALKIE_RANGE;

    // Cross-dimension settings
    public static final ModConfigSpec.BooleanValue CROSS_DIMENSIONS_ENABLED;
    public static final ModConfigSpec.BooleanValue APPLY_DIMENSION_SCALE;

    // Misc settings
    public static final ModConfigSpec.BooleanValue APPLY_RADIO_EFFECT;
    public static final ModConfigSpec.DoubleValue EFFECT_VOLUME;
    public static final ModConfigSpec.BooleanValue REQUIRE_HOTBAR_FOR_LISTENING;
    public static final ModConfigSpec.BooleanValue TRANSMIT_FROM_HAND;

    static {
        BUILDER.comment("Channel Settings").push("channels");
        MAX_CANAL = BUILDER.comment("Maximum number of channels available.")
                .defineInRange("max_canal", 16, 1, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.comment("Speaker Settings").push("speaker");
        SPEAKER_DISTANCE = BUILDER.comment("The distance (in blocks) at which the speaker can be heard.")
                .defineInRange("speaker_distance", 32, 1, Integer.MAX_VALUE);
        VOICE_DUPLICATION = BUILDER.comment("Whether the speaker duplicates the voice of the player using the walkie-talkie.")
                .define("voice_duplication", false);
        BUILDER.pop();

        BUILDER.comment("Walkie-Talkie Range Settings").push("range");
        WOODEN_WALKIETALKIE_RANGE = BUILDER.comment("Range (in blocks) of the wooden walkie-talkie.")
                .defineInRange("wooden_range", 128, 1, Integer.MAX_VALUE);
        STONE_WALKIETALKIE_RANGE = BUILDER.comment("Range (in blocks) of the stone walkie-talkie.")
                .defineInRange("stone_range", 256, 1, Integer.MAX_VALUE);
        IRON_WALKIETALKIE_RANGE = BUILDER.comment("Range (in blocks) of the iron walkie-talkie.")
                .defineInRange("iron_range", 512, 1, Integer.MAX_VALUE);
        GOLDEN_WALKIETALKIE_RANGE = BUILDER.comment("Range (in blocks) of the golden walkie-talkie.")
                .defineInRange("golden_range", 1024, 1, Integer.MAX_VALUE);
        DIAMOND_WALKIETALKIE_RANGE = BUILDER.comment("Range (in blocks) of the diamond walkie-talkie.")
                .defineInRange("diamond_range", 2048, 1, Integer.MAX_VALUE);
        NETHERITE_WALKIETALKIE_RANGE = BUILDER.comment("Range (in blocks) of the netherite walkie-talkie.")
                .defineInRange("netherite_range", 4096, 1, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.comment("Cross-Dimension Settings").push("dimensions");
        CROSS_DIMENSIONS_ENABLED = BUILDER.comment("Whether walkie-talkies work across dimensions.")
                .define("cross_dimensions_enabled", true);
        APPLY_DIMENSION_SCALE = BUILDER.comment("Whether to apply dimension distance scaling.")
                .define("apply_dimension_scale", true);
        BUILDER.pop();

        BUILDER.comment("Misc Settings").push("misc");
        APPLY_RADIO_EFFECT = BUILDER.comment("Whether to apply the radio audio effect to transmissions.")
                .define("apply_radio_effect", true);
        EFFECT_VOLUME = BUILDER.comment("Volume of the radio effect (0.0 to 1.0).")
                .defineInRange("effect_volume", 0.5, 0.0, 1.0);
        REQUIRE_HOTBAR_FOR_LISTENING = BUILDER.comment("Whether the walkie-talkie must be in the hotbar to receive transmissions.")
                .define("require_hotbar_for_listening", false);
        TRANSMIT_FROM_HAND = BUILDER.comment("Whether voice is always transmitted when the walkie-talkie is on (true), or only when holding the talk keybind (false).")
                .define("transmit_from_hand", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    /**
     * Syncs the NeoForge config spec values into the ModConfig static fields
     * so the rest of the codebase continues to work without changes.
     */
    public static void syncToModConfig() {
        ModConfig.maxCanal = MAX_CANAL.get();
        ModConfig.speakerDistance = SPEAKER_DISTANCE.get();
        ModConfig.voiceDuplication = VOICE_DUPLICATION.get();
        ModConfig.woodenWalkieTalkieRange = WOODEN_WALKIETALKIE_RANGE.get();
        ModConfig.stoneWalkieTalkieRange = STONE_WALKIETALKIE_RANGE.get();
        ModConfig.ironWalkieTalkieRange = IRON_WALKIETALKIE_RANGE.get();
        ModConfig.goldenWalkieTalkieRange = GOLDEN_WALKIETALKIE_RANGE.get();
        ModConfig.diamondWalkieTalkieRange = DIAMOND_WALKIETALKIE_RANGE.get();
        ModConfig.netheriteWalkieTalkieRange = NETHERITE_WALKIETALKIE_RANGE.get();
        ModConfig.crossDimensionsEnabled = CROSS_DIMENSIONS_ENABLED.get();
        ModConfig.applyDimensionScale = APPLY_DIMENSION_SCALE.get();
        ModConfig.applyRadioEffect = APPLY_RADIO_EFFECT.get();
        ModConfig.effectVolume = (float) (double) EFFECT_VOLUME.get();
        ModConfig.requireHotbarForListening = REQUIRE_HOTBAR_FOR_LISTENING.get();
        ModConfig.transmitFromHand = TRANSMIT_FROM_HAND.get();
    }

    /**
     * Writes the current ModConfig static field values back into the NeoForge
     * config spec values so that they are persisted to the TOML file.
     */
    public static void saveFromModConfig() {
        MAX_CANAL.set(ModConfig.maxCanal);
        SPEAKER_DISTANCE.set(ModConfig.speakerDistance);
        VOICE_DUPLICATION.set(ModConfig.voiceDuplication);
        WOODEN_WALKIETALKIE_RANGE.set(ModConfig.woodenWalkieTalkieRange);
        STONE_WALKIETALKIE_RANGE.set(ModConfig.stoneWalkieTalkieRange);
        IRON_WALKIETALKIE_RANGE.set(ModConfig.ironWalkieTalkieRange);
        GOLDEN_WALKIETALKIE_RANGE.set(ModConfig.goldenWalkieTalkieRange);
        DIAMOND_WALKIETALKIE_RANGE.set(ModConfig.diamondWalkieTalkieRange);
        NETHERITE_WALKIETALKIE_RANGE.set(ModConfig.netheriteWalkieTalkieRange);
        CROSS_DIMENSIONS_ENABLED.set(ModConfig.crossDimensionsEnabled);
        APPLY_DIMENSION_SCALE.set(ModConfig.applyDimensionScale);
        APPLY_RADIO_EFFECT.set(ModConfig.applyRadioEffect);
        EFFECT_VOLUME.set((double) ModConfig.effectVolume);
        REQUIRE_HOTBAR_FOR_LISTENING.set(ModConfig.requireHotbarForListening);
        TRANSMIT_FROM_HAND.set(ModConfig.transmitFromHand);
        SPEC.save();
    }
}


