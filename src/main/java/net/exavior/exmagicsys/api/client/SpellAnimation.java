package net.exavior.exmagicsys.api.client;

import com.mojang.blaze3d.vertex.PoseStack; // <-- IMPORT
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface SpellAnimation {
    void apply(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm, float ticksRunning, float partialTick);

    default void applyFirstPerson(PoseStack poseStack, HumanoidArm arm, float ticksRunning, float partialTick) {

    }

    static ModelPart getArmPart(HumanoidModel<?> model, HumanoidArm arm) {
        return arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
    }
}