package net.exavior.exmagicsys.network.client.toserverpackets;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent from Client to Server when the cycle key is pressed.
 */
public record ServerboundCycleSpellPacket() implements CustomPacketPayload {

    public static final Type<ServerboundCycleSpellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "cycle_spell"));
            
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCycleSpellPacket> STREAM_CODEC =
            StreamCodec.unit(new ServerboundCycleSpellPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}