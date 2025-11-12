package net.exavior.exmagicsys.network.client.toserverpackets;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent from Client to Server when the cycle key is pressed.
 */
public record ServerCycleSpellPacket() implements CustomPacketPayload {

    public static final Type<ServerCycleSpellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "cycle_spell"));
            
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerCycleSpellPacket> STREAM_CODEC =
            StreamCodec.unit(new ServerCycleSpellPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}