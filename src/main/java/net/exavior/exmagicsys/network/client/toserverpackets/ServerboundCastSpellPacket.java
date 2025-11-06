package net.exavior.exmagicsys.network.client.toserverpackets;

import io.netty.buffer.ByteBuf;
import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * A packet sent from the client to the server when the cast key is pressed.
 *
 * @param spellId The spell the client is trying to cast.
 */
public record ServerboundCastSpellPacket(ResourceLocation spellId) implements CustomPacketPayload {

    public static final Type<ServerboundCastSpellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "cast_spell"));

    public static final StreamCodec<ByteBuf, ServerboundCastSpellPacket> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(
                    ServerboundCastSpellPacket::new,
                    ServerboundCastSpellPacket::spellId
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}