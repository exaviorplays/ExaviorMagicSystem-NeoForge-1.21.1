package net.exavior.exmagicsys.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * A data object attached to the player to track their casting progress.
 */
public record CastingState(ResourceLocation spellId, CastingPhase phase, long startTime) {

    // A Codec to allow this record to be serialized for the DataAttachment
    public static final Codec<CastingState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("spell_id").forGetter(CastingState::spellId),
            CastingPhase.CODEC.fieldOf("phase").forGetter(CastingState::phase),
            Codec.LONG.fieldOf("start_time").forGetter(CastingState::startTime)
    ).apply(instance, CastingState::new));

    /**
     * An empty state, meaning the player is not casting.
     */
    public static final CastingState NONE = new CastingState(null, CastingPhase.CHARGING, 0);

    /**
     * Helper method to check if the player is actively casting.
     */
    public boolean isCasting() {
        return this.spellId != null;
    }
}