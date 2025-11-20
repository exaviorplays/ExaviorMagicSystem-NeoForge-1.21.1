package net.exavior.exmagicsys.network.server.toclientpackets;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientClearArmPosePacket(int entityId) implements CustomPacketPayload {

    public static final Type<ClientClearArmPosePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "clear_arm_pose"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientClearArmPosePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ClientClearArmPosePacket::entityId,
                    ClientClearArmPosePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}