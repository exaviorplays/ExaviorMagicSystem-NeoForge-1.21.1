package net.exavior.exmagicsys.api.spell;

import net.exavior.exmagicsys.api.client.SpellAnimations;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpellProperties {
    private Holder<SpellClassification> classification;
    private int manaCost = 0;
    private int cooldownTicks = 0;
    private int castTimeTicks = 0;
    private int chargeTimeTicks = 0;
    private int activeTimeTicks = 0;
    private int manaCostPerActiveTick = 0;
    boolean manualCost = false;
    private boolean unlearnable = false;

    private int level = 0;
    private final List<Component> description = new ArrayList<>();

    private SpellArm spellArm = SpellArm.MAIN_HAND;
    @Nullable private ResourceLocation chargeAnim = null;
    @Nullable private ResourceLocation castAnim = null;
    @Nullable private ResourceLocation activeAnim = null;

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

    public SpellProperties manualCost() {
        this.manualCost = true;
        return this;
    }

    public SpellProperties unlearnable() {
        this.unlearnable = true;
        return this;
    }

    public SpellProperties level(int level) {
        this.level = Math.max(0, level);
        return this;
    }

    public SpellProperties description(Component descriptionLine) {
        this.description.add(descriptionLine);
        return this;
    }

    public SpellProperties description(String descriptionLine) {
        this.description.add(Component.literal(descriptionLine));
        return this;
    }

    /**
     * Sets which arm(s) this spell should pose.
     */
    public SpellProperties spellArm(SpellArm arm) {
        this.spellArm = arm;
        return this;
    }

    public SpellProperties chargeAnim(ResourceLocation animId) { this.chargeAnim = animId; return this; }
    public SpellProperties castAnim(ResourceLocation animId) { this.castAnim = animId; return this; }
    public SpellProperties activeAnim(ResourceLocation animId) { this.activeAnim = animId; return this; }

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

    public boolean isManualCost() {
        return this.manualCost;
    }

    public boolean isUnlearnable() {
        return this.unlearnable;
    }

    public int getLevel() {
        return level;
    }

    public List<Component> getDescription() {
        return description;
    }

    @Nullable public ResourceLocation getChargeAnim() { return chargeAnim; }

    @Nullable public ResourceLocation getCastAnim() { return castAnim; }

    @Nullable public ResourceLocation getActiveAnim() { return activeAnim; }
}