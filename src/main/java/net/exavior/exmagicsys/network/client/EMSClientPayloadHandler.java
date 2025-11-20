package net.exavior.exmagicsys.network.client;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.network.server.toclientpackets.ClientClearArmPosePacket;
import net.exavior.exmagicsys.network.server.toclientpackets.ClientSetArmPosePacket;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EMSClientPayloadHandler {

    private static final EMSClientPayloadHandler INSTANCE = new EMSClientPayloadHandler();

    public static EMSClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleSetArmPose(ClientSetArmPosePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());
                if (entity instanceof Player player) {
                    player.setData(EMSDataAttachments.CLIENT_SPELL_ARM_POSE.get(), packet.animId());
                    player.setData(EMSDataAttachments.CLIENT_SPELL_ARM.get(), packet.arm());
                    player.setData(EMSDataAttachments.CLIENT_ANIM_START_TIME.get(), (long) player.tickCount);
                }
            }
        });
    }

    public void handleClearArmPose(ClientClearArmPosePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());
                if (entity instanceof Player player) {
                    player.removeData(EMSDataAttachments.CLIENT_SPELL_ARM_POSE.get());
                    player.removeData(EMSDataAttachments.CLIENT_SPELL_ARM.get());
                    player.removeData(EMSDataAttachments.CLIENT_ANIM_START_TIME.get());
                }
            }
        });
    }
}