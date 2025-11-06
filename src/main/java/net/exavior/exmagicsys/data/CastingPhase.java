package net.exavior.exmagicsys.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * Represents the player's current casting state.
 * CHARGING: The player is holding the key.
 * CASTING: The spell is locked in and will fire after castTime.
 */
public enum CastingPhase implements StringRepresentable {
    CHARGING,
    CASTING,
    ACTIVE;

    public static final Codec<CastingPhase> CODEC = StringRepresentable.fromEnum(CastingPhase::values);

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}