package net.exavior.exmagicsys.network.server.toclientpackets;

import io.netty.buffer.ByteBuf;
import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.spell.SpellArm;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent from Server to Client to tell the client to force a specific ArmPose.
 */
public record ClientSetArmPosePacket(HumanoidModel.ArmPose pose, SpellArm arm) implements CustomPacketPayload {

    public static final Type<ClientSetArmPosePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "set_arm_pose"));

    private static final StreamCodec<ByteBuf, HumanoidModel.ArmPose> POSE_CODEC =
            ByteBufCodecs.VAR_INT.map(
                    (id) -> HumanoidModel.ArmPose.values()[id],
                    (HumanoidModel.ArmPose e) -> e.ordinal()
            );
            
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientSetArmPosePacket> STREAM_CODEC =
            StreamCodec.composite(
                    POSE_CODEC,
                    ClientSetArmPosePacket::pose,
                    SpellArm.STREAM_CODEC,
                    ClientSetArmPosePacket::arm,
                    ClientSetArmPosePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}