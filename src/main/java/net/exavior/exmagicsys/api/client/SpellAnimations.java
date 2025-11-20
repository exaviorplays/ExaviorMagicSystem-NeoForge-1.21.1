package net.exavior.exmagicsys.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class SpellAnimations {
    private static final Map<ResourceLocation, SpellAnimation> REGISTRY = new HashMap<>();

    public static final ResourceLocation NONE = register("none", (model, entity, arm, ticks, partial) -> {
        resetPose(model, arm);
    });

    public static final ResourceLocation TWIRL = register("twirl", new SpellAnimation() {
        @Override
        public void apply(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm, float ticks, float partial) {
            resetPose(model, arm);
            float time = (ticks + partial) * 0.4F;
            SpellAnimation.getArmPart(model, arm).xRot = -1.5F + Mth.cos(time) * 0.15F;
            SpellAnimation.getArmPart(model, arm).zRot = Mth.sin(time) * 0.15F;
        }

        @Override
        public void applyFirstPerson(PoseStack poseStack, HumanoidArm arm, float ticks, float partial) {
            float time = (ticks + partial) * 0.4F;
            float dir = (arm == HumanoidArm.RIGHT) ? 1.0f : -1.0f;
            poseStack.mulPose(Axis.ZP.rotation(Mth.sin(time) * 0.15F * dir));
            poseStack.mulPose(Axis.XP.rotation(Mth.cos(time) * 0.1F));
        }
    });

    public static final ResourceLocation SWIPE_HORIZONTAL = register("swipe_horizontal", new SpellAnimation() {
        @Override
        public void apply(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm, float ticks, float partial) {
            resetPose(model, arm);
            float progress = Mth.clamp((ticks + partial) / 6.0F, 0, 1);
            progress = (float) (1 - Math.pow(1 - progress, 3));

            ModelPart armPart = SpellAnimation.getArmPart(model, arm);
            armPart.xRot = -1.2F;
            float startY = (arm == HumanoidArm.RIGHT) ? 0.1F : -0.1F;
            float endY = (arm == HumanoidArm.RIGHT) ? -0.5F : 0.5F;
            armPart.yRot = Mth.lerp(progress, startY, endY);
        }

        @Override
        public void applyFirstPerson(PoseStack poseStack, HumanoidArm arm, float ticks, float partial) {
            float progress = Mth.clamp((ticks + partial) / 6.0F, 0, 1);
            progress = (float) (1 - Math.pow(1 - progress, 3));
            float dir = (arm == HumanoidArm.RIGHT) ? 1.0f : -1.0f;

            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(progress, 10, -20) * dir));
            poseStack.translate(0, 0, -0.1 * progress);
        }
    });

    public static final ResourceLocation SWIPE_UP = register("swipe_up", new SpellAnimation() {
        @Override
        public void apply(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm, float ticks, float partial) {
            resetPose(model, arm);
            float progress = Mth.clamp((ticks + partial) / 5.0F, 0, 1);
            progress = (float) (1 - Math.pow(1 - progress, 3));

            ModelPart part = SpellAnimation.getArmPart(model, arm);
            part.xRot = Mth.lerp(progress, -0.5F, -2.2F);
        }

        @Override
        public void applyFirstPerson(PoseStack poseStack, HumanoidArm arm, float ticks, float partial) {
            float progress = Mth.clamp((ticks + partial) / 5.0F, 0, 1);
            float eased = (float) (1 - Math.pow(1 - progress, 3));

            float yStart = 0.1F;
            float yEnd = -0.6F;
            poseStack.translate(0, Mth.lerp(eased, yStart, yEnd), -0.2);

            float rotStart = -5.0F;
            float rotEnd = 45.0F;
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(eased, rotStart, rotEnd)));
        }
    });

    public static final ResourceLocation RAISE_HOLD = register("raise_hold", new SpellAnimation() {
        @Override
        public void apply(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm, float ticks, float partial) {
            resetPose(model, arm);
            ModelPart armPart = SpellAnimation.getArmPart(model, arm);
            armPart.xRot = -3.0F;
            armPart.zRot = (arm == HumanoidArm.RIGHT) ? 0.1F : -0.1F;
            float bob = Mth.sin((ticks + partial) * 0.1F) * 0.05F;
            armPart.xRot += bob;
        }

        @Override
        public void applyFirstPerson(PoseStack poseStack, HumanoidArm arm, float ticks, float partial) {
            poseStack.translate(0, -0.2, -0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(-45));
        }
    });

    public static final ResourceLocation THRUST_HOLD = register("thrust_hold", new SpellAnimation() {
        @Override
        public void apply(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm, float ticks, float partial) {
            resetPose(model, arm);
            ModelPart part = SpellAnimation.getArmPart(model, arm);
            part.xRot = -1.57F;
            part.yRot = (arm == HumanoidArm.RIGHT) ? -0.1F : 0.1F;
        }

        @Override
        public void applyFirstPerson(PoseStack poseStack, HumanoidArm arm, float ticks, float partial) {
            poseStack.translate(0, 0, -0.4);
            float shake = Mth.sin(ticks * 0.8F) * 0.01F;
            poseStack.translate(shake, shake, 0);
        }
    });

    public static final ResourceLocation BLOCK = register("block", (model, entity, arm, ticks, partial) -> {
        if (arm == HumanoidArm.RIGHT) model.rightArmPose = HumanoidModel.ArmPose.BLOCK;
        else model.leftArmPose = HumanoidModel.ArmPose.BLOCK;
    });

    public static final ResourceLocation BOW = register("bow", (model, entity, arm, ticks, partial) -> {
        if (arm == HumanoidArm.RIGHT) model.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
        else model.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
    });

    private static void resetPose(HumanoidModel<?> model, HumanoidArm arm) {
        if (arm == HumanoidArm.RIGHT) model.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        else model.leftArmPose = HumanoidModel.ArmPose.EMPTY;
    }

    public static ResourceLocation register(String name, SpellAnimation animation) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, name);
        REGISTRY.put(id, animation);
        return id;
    }

    public static SpellAnimation get(ResourceLocation id) {
        return REGISTRY.getOrDefault(id, REGISTRY.get(NONE));
    }
}