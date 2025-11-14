package net.exavior.exmagicsys.api.spell;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum CastSource implements StringRepresentable {
    KEYBIND,
    ITEM;

    public static final Codec<CastSource> CODEC = StringRepresentable.fromEnum(CastSource::values);

    public static final StreamCodec<ByteBuf, CastSource> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(
                    (id) -> CastSource.values()[id],
                    (CastSource e) -> e.ordinal()
            );

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}