package net.exavior.exmagicsys.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.exavior.exmagicsys.api.spell.CastSource; // <-- IMPORT
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand; // <-- IMPORT
import javax.annotation.Nullable; // <-- IMPORT

/**
 * A data object attached to the player to track their casting progress.
 */
public record CastingState(
        @Nullable ResourceLocation spellId,
        CastingPhase phase,
        long startTime,
        CastSource source,
        @Nullable InteractionHand hand
) {

    private static final Codec<InteractionHand> HAND_CODEC = Codec.BOOL.xmap(
            b -> b ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
            hand -> hand == InteractionHand.MAIN_HAND
    );

    public static final Codec<CastingState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("spell_id").forGetter(CastingState::spellId),
            CastingPhase.CODEC.fieldOf("phase").forGetter(CastingState::phase),
            Codec.LONG.fieldOf("start_time").forGetter(CastingState::startTime),
            CastSource.CODEC.fieldOf("source").forGetter(CastingState::source),
            HAND_CODEC.fieldOf("hand").forGetter(CastingState::hand)
    ).apply(instance, CastingState::new));

    /**
     * An empty state, meaning the player is not casting.
     */
    public static final CastingState NONE = new CastingState(null, CastingPhase.CHARGING, 0, CastSource.KEYBIND, null);

    /**
     * Helper method to check if the player is actively casting.
     */
    public boolean isCasting() {
        return this.spellId != null;
    }
}