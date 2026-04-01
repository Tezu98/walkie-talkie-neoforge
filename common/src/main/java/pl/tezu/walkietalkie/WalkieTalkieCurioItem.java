package pl.tezu.walkietalkie;

import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Provides Curios behavior for all walkie-talkie items.
 * This class is intentionally separate from WalkieTalkieItem so that
 * WalkieTalkieItem can load without Curios on the classpath.
 * It is only referenced from ModCurioItems, which is only touched when
 * Curios is present (registered via FMLCommonSetupEvent).
 */
public class WalkieTalkieCurioItem implements ICurioItem {
    // No extra behavior needed beyond the default ICurioItem implementation.
    // The slot assignment and item tag handle equipping validation.
}

