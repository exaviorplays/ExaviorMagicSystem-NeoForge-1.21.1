package net.exavior.exmagicsys.api.spell;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum SpellArm implements StringRepresentable {
    MAIN_HAND,
    OFF_HAND,
    BOTH;

    public static final Codec<SpellArm> CODEC = StringRepresentable.fromEnum(SpellArm::values);

    public static final StreamCodec<ByteBuf, SpellArm> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(
                    (id) -> SpellArm.values()[id],
                    (SpellArm e) -> e.ordinal()
            );

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}