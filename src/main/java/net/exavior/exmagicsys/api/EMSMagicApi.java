package net.exavior.exmagicsys.api;

import net.exavior.exmagicsys.EMSConfig;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.api.spell.SpellArm;
import net.exavior.exmagicsys.network.server.toclientpackets.ClientClearArmPosePacket;
import net.exavior.exmagicsys.network.server.toclientpackets.ClientSetArmPosePacket;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

public class EMSMagicApi {

    /**
     * Increases the Mana of the given player by the given increment.
     */
    public static int incrementMana(Player player, int increment) {
        int newMana = player.getData(EMSDataAttachments.MANA_VALUE.get()) + increment;
        player.setData(EMSDataAttachments.MANA_VALUE.get(), newMana);
        return newMana;
    }

    /**
     * Decreases the Mana of the given player by the given decrement.
     */
    public static int decrementMana(Player player, int decrement) {
        int currentMana = player.getData(EMSDataAttachments.MANA_VALUE.get());
        int newMana = currentMana - decrement;
        player.setData(EMSDataAttachments.MANA_VALUE.get(), newMana);

        if (decrement > 0 && EMSConfig.SERVER.manaDecreaseCauseCooldown.get()) {
            triggerManaRegenCooldown(player);
        }

        return newMana;
    }

    /**
     * Set the Mana of the given player to a given amount.
     */
    public static int setMana(Player player, int setAmount) {
        int currentMana = player.getData(EMSDataAttachments.MANA_VALUE.get());
        if (setAmount < currentMana && EMSConfig.SERVER.manaDecreaseCauseCooldown.get()) {
            triggerManaRegenCooldown(player);
        }

        player.setData(EMSDataAttachments.MANA_VALUE.get(), setAmount);
        return setAmount;
    }

    /**
     * Increases the Max Mana of the given player by the given increment.
     */
    public static int incrementMaxMana(Player player, int increment) {
        int newMaxMana = player.getData(EMSDataAttachments.MAX_MANA_VALUE.get()) + increment;
        player.setData(EMSDataAttachments.MAX_MANA_VALUE.get(), newMaxMana);
        return newMaxMana;
    }

    /**
     * Decreases the Max Mana of the given player by the given decrement.
     */
    public static int decrementMaxMana(Player player, int decrement) {
        return incrementMaxMana(player, -decrement);
    }

    /**
     * Set the Max Mana of the given player to a given amount.
     */
    public static int setMaxMana(Player player, int setAmount) {
        player.setData(EMSDataAttachments.MAX_MANA_VALUE.get(), setAmount);
        return setAmount;
    }

    /**
     * Makes a player learn a spell.
     */
    public static boolean learnSpell(Player player, ResourceLocation spellId) {
        Registry<Spell> spellRegistry = player.level().registryAccess().registryOrThrow(EMSRegistries.SPELL_REGISTRY_KEY);
        Spell spell = spellRegistry.get(spellId);

        if (spell == null || spell.isUnlearnable()) {
            return false;
        }

        Set<ResourceLocation> currentSpells = player.getData(EMSDataAttachments.KNOWN_SPELLS.get());
        Set<ResourceLocation> newSpells = new HashSet<>(currentSpells);
        boolean added = newSpells.add(spellId);
        if (added) {
            player.setData(EMSDataAttachments.KNOWN_SPELLS.get(), newSpells);
        }
        return added;
    }

    /**
     * Makes a player unlearn a spell.
     */
    public static boolean unlearnSpell(Player player, ResourceLocation spellId) {
        Set<ResourceLocation> currentSpells = player.getData(EMSDataAttachments.KNOWN_SPELLS.get());
        Set<ResourceLocation> newSpells = new HashSet<>(currentSpells);
        boolean removed = newSpells.remove(spellId);

        if (removed) {
            player.setData(EMSDataAttachments.KNOWN_SPELLS.get(), newSpells);

            List<ResourceLocation> equipped = player.getData(EMSDataAttachments.EQUIPPED_SPELLS.get());
            List<ResourceLocation> newEquipped = new ArrayList<>(equipped);
            boolean changed = false;
            for(int i = 0; i < newEquipped.size(); i++) {
                if(spellId.equals(newEquipped.get(i))) {
                    newEquipped.set(i, null);
                    changed = true;
                }
            }
            if(changed) {
                player.setData(EMSDataAttachments.EQUIPPED_SPELLS.get(), newEquipped);
            }
        }
        return removed;
    }
    /**
     * Makes a player unlearn all known spells and clears their selected spell.
     */
    public static void unlearnAllSpells(Player player) {
        if (!player.getData(EMSDataAttachments.KNOWN_SPELLS.get()).isEmpty()) {
            player.setData(EMSDataAttachments.KNOWN_SPELLS.get(), new HashSet<ResourceLocation>());
        }

        player.setData(EMSDataAttachments.EQUIPPED_SPELLS.get(), new ArrayList<>(Collections.nCopies(4, null)));
        player.setData(EMSDataAttachments.ACTIVE_SPELL_SLOT.get(), 0);
    }

    /**
     * Checks if a player knows a specific spell.
     */
    public static boolean knowsSpell(Player player, ResourceLocation spellId) {
        return player.getData(EMSDataAttachments.KNOWN_SPELLS.get()).contains(spellId);
    }

    /**
     * Gets an unmodifiable set of all spell IDs the player knows.
     */
    public static Set<ResourceLocation> getKnownSpells(Player player) {
        return Collections.unmodifiableSet(player.getData(EMSDataAttachments.KNOWN_SPELLS.get()));
    }

    /**
     * Gets the player's 4 equipped spells. The list *always* has 4 elements,
     * which can be null.
     */
    public static List<ResourceLocation> getEquippedSpells(Player player) {
        return player.getData(EMSDataAttachments.EQUIPPED_SPELLS.get());
    }

    /**
     * Gets the player's currently active spell slot index (0-3).
     */
    public static int getActiveSpellSlot(Player player) {
        return player.getData(EMSDataAttachments.ACTIVE_SPELL_SLOT.get());
    }

    /**
     * Gets the ResourceLocation of the player's currently active spell.
     * Can return null if the active slot is empty.
     */
    @Nullable
    public static ResourceLocation getActiveSpell(Player player) {
        List<ResourceLocation> spells = getEquippedSpells(player);
        int slot = getActiveSpellSlot(player);
        if (spells.size() == 4 && slot >= 0 && slot < 4) {
            return spells.get(slot);
        }
        return null;
    }

    /**
     * Server-side logic to set a spell in an equipment slot.
     */
    public static void setEquippedSpell(Player player, int slot, @Nullable ResourceLocation spellId) {
        if (slot < 0 || slot >= 4) return;
        if (spellId != null && !knowsSpell(player, spellId)) return;

        List<ResourceLocation> currentSpells = player.getData(EMSDataAttachments.EQUIPPED_SPELLS.get());
        List<ResourceLocation> newSpells = new ArrayList<>(currentSpells);

        newSpells.set(slot, spellId);
        player.setData(EMSDataAttachments.EQUIPPED_SPELLS.get(), newSpells);
    }

    /**
     * Server-side logic to cycle to the next available equipped spell.
     */
    public static void cycleActiveSpell(Player player) {
        List<ResourceLocation> spells = getEquippedSpells(player);
        int currentSlot = getActiveSpellSlot(player);

        for (int i = 1; i <= 3; i++) {
            int nextSlot = (currentSlot + i) % 4; // Wrap around
            if (spells.get(nextSlot) != null) {
                player.setData(EMSDataAttachments.ACTIVE_SPELL_SLOT.get(), nextSlot);
                return;
            }
        }
    }


    /**
     * Checks if a player has enough mana and is not on cooldown for a spell.
     */
    public static boolean canCastSpell(Player player, Spell spell, ResourceLocation spellId) {
        int currentMana = player.getData(EMSDataAttachments.MANA_VALUE.get()); // <-- Added .get()
        if (currentMana < spell.getManaCost()) {
            // player.sendSystemMessage(Component.translatable("exmagicsys.feedback.no_mana"));
            // player.displayClientMessage(Component.translatable("exmagicsys.feedback.no_mana"), true);
            return false;
        }

        Map<ResourceLocation, Long> cooldowns = player.getData(EMSDataAttachments.SPELL_COOLDOWNS.get()); // <-- Added .get()
        long currentTime = player.level().getGameTime();
        long expirationTime = cooldowns.getOrDefault(spellId, 0L);

        if (currentTime < expirationTime) {
            long ticksRemaining = expirationTime - currentTime;
            // player.sendSystemMessage(Component.translatable("exmagicsys.feedback.on_cooldown", String.format("%.1f", ticksRemaining / 20.0f)));
            // player.displayClientMessage(Component.translatable("exmagicsys.feedback.on_cooldown", String.format("%.1f", ticksRemaining / 20.0f)), true);
            return false;
        }

        return true;
    }

    /**
     * Consumes mana and applies the cooldown for a spell.
     * This is now "immutable-safe".
     */
    public static void applySpellCosts(Player player, Spell spell, ResourceLocation spellId) {
        int currentMana = player.getData(EMSDataAttachments.MANA_VALUE.get());
        player.setData(EMSDataAttachments.MANA_VALUE.get(), currentMana - spell.getManaCost());

        triggerManaRegenCooldown(player);

        if (spell.getCooldownTicks() > 0) {
            Map<ResourceLocation, Long> currentCooldowns = player.getData(EMSDataAttachments.SPELL_COOLDOWNS.get());
            Map<ResourceLocation, Long> newCooldowns = new HashMap<>(currentCooldowns);
            long expirationTime = player.level().getGameTime() + spell.getCooldownTicks();
            newCooldowns.put(spellId, expirationTime);
            player.setData(EMSDataAttachments.SPELL_COOLDOWNS.get(), newCooldowns);
        }
    }

    /**
     * Checks if the player has enough mana for an active tick.
     * This does NOT consume mana.
     * @return true if the player has enough mana.
     */
    public static boolean hasEnoughManaForActiveTick(Player player, Spell spell) {
        int cost = spell.getManaCostPerActiveTick();
        if (cost == 0) {
            return true;
        }
        return player.getData(EMSDataAttachments.MANA_VALUE.get()) >= cost;
    }

    /**
     * Tries to consume the mana for a spell's active tick.
     * This is intended to be called by the spell itself inside activeTick().
     * @return true if mana was successfully consumed, false if not (out of mana).
     */
    public static boolean consumeActiveTickMana(Player player, Spell spell) {
        int cost = spell.getManaCostPerActiveTick();
        if (cost == 0) {
            return true;
        }

        int currentMana = player.getData(EMSDataAttachments.MANA_VALUE.get());
        if (currentMana < cost) {
            return false;
        }

        triggerManaRegenCooldown(player);

        player.setData(EMSDataAttachments.MANA_VALUE.get(), currentMana - cost);
        return true;
    }

    /**
     * Tells a player's client to play a spell ArmPose.
     * If pose is null, it stops the pose instead.
     */
    public static void playArmPose(ServerPlayer player, @Nullable HumanoidModel.ArmPose pose, SpellArm arm) {
        if (pose != null) {
            PacketDistributor.sendToPlayer(player, new ClientSetArmPosePacket(pose, arm));
        } else {
            stopArmPose(player);
        }
    }

    /**
     * Tells a player's client to stop forcing an ArmPose.
     */
    public static void stopArmPose(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new ClientClearArmPosePacket());
    }

    // ----------------------------------------------------------------------------

    /**
     * A private helper to apply the mana regen cooldown based on the config.
     */
    private static void triggerManaRegenCooldown(Player player) {
        long cooldownTime = EMSConfig.SERVER.manaRegenCooldown.get();

        if (cooldownTime > 0) {
            long gameTime = player.level().getGameTime();
            player.setData(EMSDataAttachments.MANA_REGEN_COOLDOWN_UNTIL.get(), gameTime + cooldownTime);
        }
    }
}