package net.exavior.exmagicsys.network.client.toserverpackets;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent from Client to Server when the cast key is RELEASED.
 */
public record ServerReleaseCastKeyPacket() implements CustomPacketPayload {

    public static final Type<ServerReleaseCastKeyPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "release_cast_key"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerReleaseCastKeyPacket> STREAM_CODEC =
            StreamCodec.unit(new ServerReleaseCastKeyPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}