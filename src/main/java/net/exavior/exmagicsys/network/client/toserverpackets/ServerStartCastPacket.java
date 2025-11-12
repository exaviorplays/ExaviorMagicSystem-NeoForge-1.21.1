package net.exavior.exmagicsys.network.client.toserverpackets;
// (Or your correct network package)

import io.netty.buffer.ByteBuf;
import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent from Client to Server when the cast key is PRESSED.
 */
public record ServerStartCastPacket(ResourceLocation spellId) implements CustomPacketPayload {

    public static final Type<ServerStartCastPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "start_cast"));

    public static final StreamCodec<ByteBuf, ServerStartCastPacket> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(
                    ServerStartCastPacket::new,
                    ServerStartCastPacket::spellId
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}