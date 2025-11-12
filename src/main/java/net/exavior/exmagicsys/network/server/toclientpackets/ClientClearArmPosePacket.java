package net.exavior.exmagicsys.network.server.toclientpackets;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent from Server to Client to tell the client to stop forcing an ArmPose.
 */
public record ClientClearArmPosePacket() implements CustomPacketPayload {

    public static final Type<ClientClearArmPosePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "clear_arm_pose"));
            
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientClearArmPosePacket> STREAM_CODEC =
            StreamCodec.unit(new ClientClearArmPosePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}