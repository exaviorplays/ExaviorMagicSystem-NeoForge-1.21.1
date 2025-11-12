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
public record ServerCastSpellPacket(ResourceLocation spellId) implements CustomPacketPayload {

    public static final Type<ServerCastSpellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "cast_spell"));

    public static final StreamCodec<ByteBuf, ServerCastSpellPacket> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(
                    ServerCastSpellPacket::new,
                    ServerCastSpellPacket::spellId
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}