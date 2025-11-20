package net.exavior.exmagicsys.event;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.client.SpellAnimation;
import net.exavior.exmagicsys.api.client.SpellAnimations;
import net.exavior.exmagicsys.api.spell.SpellArm;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = ExaviorMagicSystem.MODID, value = Dist.CLIENT)
public class EMSRenderEvents {

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();

        ResourceLocation animId = player.getData(EMSDataAttachments.CLIENT_SPELL_ARM_POSE.get());
        if (animId == null) return;

        SpellArm spellArm = player.getData(EMSDataAttachments.CLIENT_SPELL_ARM.get());
        long startTime = player.getData(EMSDataAttachments.CLIENT_ANIM_START_TIME.get());

        float ticksElapsed = (player.tickCount - startTime);

        SpellAnimation animation = SpellAnimations.get(animId);

        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        HumanoidArm mainArm = player.getMainArm();

        if (spellArm == SpellArm.MAIN_HAND) {
            animation.apply(model, player, mainArm, ticksElapsed, event.getPartialTick());

        } else if (spellArm == SpellArm.OFF_HAND) {
            animation.apply(model, player, mainArm.getOpposite(), ticksElapsed, event.getPartialTick());

        } else if (spellArm == SpellArm.BOTH) {
            animation.apply(model, player, HumanoidArm.RIGHT, ticksElapsed, event.getPartialTick());
            animation.apply(model, player, HumanoidArm.LEFT, ticksElapsed, event.getPartialTick());
        }
    }
}