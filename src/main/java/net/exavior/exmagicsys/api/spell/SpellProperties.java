package net.exavior.exmagicsys.api.spell;

import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

public class SpellProperties {
    private Holder<SpellClassification> classification;
    private int manaCost = 0;
    private int cooldownTicks = 0;
    private int castTimeTicks = 0;
    private int chargeTimeTicks = 0;
    private int activeTimeTicks = 0;
    private int manaCostPerActiveTick = 0;
    private boolean unlearnable = false;

    private SpellArm spellArm = SpellArm.MAIN_HAND;
    @Nullable
    private HumanoidModel.ArmPose chargeArmPose = null;
    @Nullable
    private HumanoidModel.ArmPose castArmPose = null;
    @Nullable
    private HumanoidModel.ArmPose activeArmPose = null;


    // By default, a spell has no classification.
    // We use a Supplier for the default value to avoid loading registries too early.
    public SpellProperties() {
        this.classification = EMSRegistries.CLASSIFICATION_NONE;
    }

    public SpellProperties classification(Holder<SpellClassification> classification) {
        this.classification = classification;
        return this;
    }

    public SpellProperties manaCost(int manaCost) {
        this.manaCost = manaCost;
        return this;
    }

    public SpellProperties cooldownTicks(int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
        return this;
    }

    public SpellProperties castTimeTicks(int castTimeTicks) {
        this.castTimeTicks = castTimeTicks;
        return this;
    }

    public SpellProperties chargeTimeTicks(int chargeTimeTicks) {
        this.chargeTimeTicks = chargeTimeTicks;
        return this;
    }

    public SpellProperties activeTimeTicks(int activeTimeTicks) {
        this.activeTimeTicks = activeTimeTicks;
        return this;
    }

    public SpellProperties manaCostPerActiveTick(int manaCost) {
        this.manaCostPerActiveTick = manaCost;
        return this;
    }

    public SpellProperties unlearnable() {
        this.unlearnable = true;
        return this;
    }

    /**
     * Sets which arm(s) this spell should pose.
     */
    public SpellProperties spellArm(SpellArm arm) {
        this.spellArm = arm;
        return this;
    }

    /**
     * Sets the ArmPose to play during the CHARGING phase.
     * e.g., HumanoidModel.ArmPose.BOW_AND_ARROW
     */
    public SpellProperties chargeArmPose(@Nullable HumanoidModel.ArmPose pose) {
        this.chargeArmPose = pose;
        return this;
    }

    /**
     * Sets the ArmPose to play during the CASTING phase.
     * e.g., HumanoidModel.ArmPose.THROW_SPEAR
     */
    public SpellProperties castArmPose(@Nullable HumanoidModel.ArmPose pose) {
        this.castArmPose = pose;
        return this;
    }

    /**
     * Sets the ArmPose to play during the ACTIVE phase.
     * e.g., HumanoidModel.ArmPose.BLOCK
     */
    public SpellProperties activeArmPose(@Nullable HumanoidModel.ArmPose pose) {
        this.activeArmPose = pose;
        return this;
    }

    public Holder<SpellClassification> getClassification() {
        return classification;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public int getCastTimeTicks() {
        return castTimeTicks;
    }

    public int getChargeTimeTicks() {
        return chargeTimeTicks;
    }

    public int getActiveTimeTicks() {
        return activeTimeTicks;
    }

    public int getManaCostPerActiveTick() {
        return manaCostPerActiveTick;
    }

    public SpellArm getSpellArm() {
        return spellArm;
    }

    public boolean isUnlearnable() {
        return this.unlearnable;
    }

    @Nullable
    public HumanoidModel.ArmPose getChargeArmPose() {
        return chargeArmPose;
    }

    @Nullable
    public HumanoidModel.ArmPose getCastArmPose() {
        return castArmPose;
    }

    @Nullable
    public HumanoidModel.ArmPose getActiveArmPose() {
        return activeArmPose;
    }
}