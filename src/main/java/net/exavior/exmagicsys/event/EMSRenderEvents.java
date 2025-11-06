package net.exavior.exmagicsys.event;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.spell.SpellArm;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = ExaviorMagicSystem.MODID, value = Dist.CLIENT)
public class EMSRenderEvents {

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        AbstractClientPlayer player = Minecraft.getInstance().player;

        if (player == null) return;

        HumanoidModel.ArmPose pose = player.getData(EMSDataAttachments.CLIENT_SPELL_ARM_POSE.get());
        SpellArm spellArm = player.getData(EMSDataAttachments.CLIENT_SPELL_ARM.get());

        if (pose == HumanoidModel.ArmPose.EMPTY) {
            return;
        }

        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        HumanoidArm mainArm = player.getMainArm();

        if (spellArm == SpellArm.MAIN_HAND) {
            if (mainArm == HumanoidArm.RIGHT) model.rightArmPose = pose;
            else model.leftArmPose = pose;
        } else if (spellArm == SpellArm.OFF_HAND) {
            if (mainArm == HumanoidArm.RIGHT) model.leftArmPose = pose;
            else model.rightArmPose = pose;
        } else if (spellArm == SpellArm.BOTH) {
            model.rightArmPose = pose;
            model.leftArmPose = pose;
        }
    }
}