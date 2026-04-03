package pl.tezu.walkietalkie.neoforge;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import pl.tezu.walkietalkie.block.SpeakerBlock;
import pl.tezu.walkietalkie.block.entity.SpeakerBlockEntity;
import pl.tezu.walkietalkie.radio.Canal;
import pl.tezu.walkietalkie.radio.Member;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Handles speakers that are mounted inside a Create contraption.
 * <p>
 * When Create assembles a contraption it converts live {@link SpeakerBlockEntity}
 * instances into raw NBT stored inside {@link Contraption#getBlocks()}.
 * Because no Java object is ever constructed the normal
 * {@code SpeakerBlockEntity.SPEAKERS} map never gets an entry for those speakers,
 * making them silent.
 * <p>
 * This class iterates every {@link AbstractContraptionEntity} in the world each
 * server tick, finds activated speaker blocks in their NBT, derives a stable UUID
 * from the contraption's UUID + block-local position, converts the local position
 * to a world-space position, and injects a virtual {@link Member} so voice-chat
 * audio is routed through it correctly.
 */
public class CreateContraptionIntegration {

    /**
     * Called every server tick from {@link Member#serverTick} when Create is loaded.
     * Scans all live contraption entities across every dimension and registers any
     * activated speaker blocks as {@link Member} instances.
     */
    public static void addContraptionSpeakers(MinecraftServer server, List<Member> members) {
        for (ServerLevel level : server.getAllLevels()) {
            // Use getAllEntities() (vanilla API) instead of getEntities(EntityTypeTest, ...)
            // to avoid a compile-time dependency on
            // net.neoforged.neoforge.entity.IEntityWithComplexSpawn which is not present on
            // the common-module classpath.
            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof AbstractContraptionEntity contraptionEntity)) continue;
                if (entity.isRemoved()) continue;

                Contraption contraption = contraptionEntity.getContraption();
                if (contraption == null) continue;

                for (StructureTemplate.StructureBlockInfo info : contraption.getBlocks().values()) {
                    if (!(info.state().getBlock() instanceof SpeakerBlock)) continue;
                    if (info.nbt() == null) continue;

                    boolean activated = info.nbt().getBoolean(SpeakerBlockEntity.TAG_KEY_ACTIVATE);
                    if (!activated) continue;

                    int canal = info.nbt().getInt(SpeakerBlockEntity.TAG_KEY_CANAL);
                    if (canal <= 0) canal = 1;

                    Set<Canal> canals = Set.of(Canal.getOrCreate(canal));

                    // Build a deterministic UUID so the same speaker maps to the same Member
                    // across ticks without needing to persist any state.
                    String key = contraptionEntity.getUUID() + ":" + info.pos().toShortString();
                    UUID speakerUUID = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));

                    // Convert contraption-local block position to current world-space position.
                    Vec3 worldPos = contraptionEntity.toGlobalVector(Vec3.atCenterOf(info.pos()), 1.0f);

                    Member member = Member.getOrCreate(speakerUUID, worldPos, level, canals);
                    if (member != null) members.add(member);
                }
            }
        }
    }
}

