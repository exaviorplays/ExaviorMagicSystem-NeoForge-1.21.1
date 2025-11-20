package net.exavior.exmagicsys.network.server.toclientpackets;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.spell.SpellArm;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientSetArmPosePacket(ResourceLocation animId, SpellArm arm) implements CustomPacketPayload {

    public static final Type<ClientSetArmPosePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "set_arm_pose"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientSetArmPosePacket> STREAM_CODEC =
            StreamCodec.composite(
                    EMSDataAttachments.NULLABLE_RL_STREAM_CODEC,
                    ClientSetArmPosePacket::animId,
                    SpellArm.STREAM_CODEC,
                    ClientSetArmPosePacket::arm,
                    ClientSetArmPosePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}