package net.exavior.exmagicsys.api.client;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.client.SpellAnimation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

public class SpellAnimations {

    private static final Map<ResourceLocation, SpellAnimation> REGISTRY = new HashMap<>();

    public static final ResourceLocation NONE = register("none", (model, entity, arm, ticks, partial) -> {});

    public static final ResourceLocation TWIRL = register("twirl", (model, entity, arm, ticks, partial) -> {
        float time = (ticks + partial) * 0.4F;
        SpellAnimation.getArmPart(model, arm).xRot = -1.5F + Mth.cos(time) * 0.3F;
        SpellAnimation.getArmPart(model, arm).zRot = Mth.sin(time) * 0.3F;
    });

    public static final ResourceLocation THRUST = register("thrust", (model, entity, arm, ticks, partial) -> {
        float progress = (ticks + partial) % 10;
        float angle;
        if (progress < 3) {
            angle = -1.0F + (progress / 3.0F) * 0.5F;
        } else {
            angle = -0.5F - ((progress - 3) / 7.0F) * 1.2F;
        }
        SpellAnimation.getArmPart(model, arm).xRot = angle;
    });

    public static final ResourceLocation BLOCK = register("block", (model, entity, arm, ticks, partial) -> {
        if (arm == net.minecraft.world.entity.HumanoidArm.RIGHT) {
            model.rightArmPose = HumanoidModel.ArmPose.BLOCK;
        } else {
            model.leftArmPose = HumanoidModel.ArmPose.BLOCK;
        }
    });

    public static final ResourceLocation BOW = register("bow", (model, entity, arm, ticks, partial) -> {
        if (arm == net.minecraft.world.entity.HumanoidArm.RIGHT) {
            model.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
        } else {
            model.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
        }
    });

    public static ResourceLocation register(String name, SpellAnimation animation) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, name);
        REGISTRY.put(id, animation);
        return id;
    }

    public static SpellAnimation get(ResourceLocation id) {
        return REGISTRY.getOrDefault(id, REGISTRY.get(NONE));
    }
}