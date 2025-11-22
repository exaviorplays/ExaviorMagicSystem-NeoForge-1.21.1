package net.exavior.exmagicsys.api.spell;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * The base class for all spells.
 * Modders will create new classes extending this one to define their spell's logic.
 * These are registered into a custom registry.
 */
public abstract class Spell {
    private final SpellProperties properties;

    private final Holder<SpellClassification> classification;
    private final int manaCost;
    private final int cooldownTicks;
    private final int castTimeTicks;
    private final int chargeTimeTicks;
    private final int activeTimeTicks;
    private final int manaCostPerActiveTick;
    private final SpellArm spellArm;
    private final boolean unlearnable;
    @Nullable private final ResourceLocation chargeAnim;
    @Nullable private final ResourceLocation castAnim;
    @Nullable private final ResourceLocation activeAnim;


    public Spell(SpellProperties properties) {
        this.properties = properties;
        this.classification = properties.getClassification();
        this.manaCost = properties.getManaCost();
        this.cooldownTicks = properties.getCooldownTicks();
        this.castTimeTicks = properties.getCastTimeTicks();
        this.chargeTimeTicks = properties.getChargeTimeTicks();
        this.activeTimeTicks = properties.getActiveTimeTicks();
        this.manaCostPerActiveTick = properties.getManaCostPerActiveTick();
        this.unlearnable = properties.isUnlearnable();
        this.spellArm = properties.getSpellArm();
        this.chargeAnim = properties.getChargeAnim();
        this.castAnim = properties.getCastAnim();
        this.activeAnim = properties.getActiveAnim();
    }


    /**
     * The core logic of the spell. This is what runs when the spell is cast.
     *
     * @param level
     * @param player
     * @param stack
     */
    public abstract void cast(ServerLevel level, ServerPlayer player, @Nullable ItemStack stack);

    /**
     * Called every tick a spell is in its "ACTIVE" phase.
     * The spell is responsible for its own logic during this time.
     */
    public void activeTick(ServerLevel level, ServerPlayer player, @Nullable ItemStack stack) {

    }

    public void chargeTick(ServerLevel level, ServerPlayer player) {

    }

    public void castTick(ServerLevel level, ServerPlayer player) {

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

    public boolean isManualCost() {
        return properties.manualCost;
    }

    public boolean isUnlearnable() {
        return this.unlearnable;
    }

    @Nullable public ResourceLocation getChargeAnim() { return chargeAnim; }

    @Nullable public ResourceLocation getCastAnim() { return castAnim; }

    @Nullable public ResourceLocation getActiveAnim() { return activeAnim; }
}