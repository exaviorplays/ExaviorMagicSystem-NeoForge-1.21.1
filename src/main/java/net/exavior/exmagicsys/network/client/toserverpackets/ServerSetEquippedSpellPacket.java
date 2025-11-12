package net.exavior.exmagicsys.network.client.toserverpackets;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Sent from Client to Server when selecting a spell in the GUI.
 */
public record ServerSetEquippedSpellPacket(int slot, @Nullable ResourceLocation spellId) implements CustomPacketPayload {

    public static final Type<ServerSetEquippedSpellPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "set_equipped_spell"));
            
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerSetEquippedSpellPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ServerSetEquippedSpellPacket::slot,
                    EMSDataAttachments.NULLABLE_RL_STREAM_CODEC,
                    ServerSetEquippedSpellPacket::spellId,
                    ServerSetEquippedSpellPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}