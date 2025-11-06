package net.exavior.exmagicsys.event;

import net.exavior.exmagicsys.EMSConfig;
import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.data.CastingPhase;
import net.exavior.exmagicsys.data.CastingState;
import net.exavior.exmagicsys.examplemod.ExampleModRegistries;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ExaviorMagicSystem.MODID)
public class EMSEvents {

    /**
     * Was for testing. Can be used as an example for
     * how the player can learn spells and unlearn all spells.
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            //EMSMagicApi.unlearnAllSpells(player);

            //ResourceLocation spellId = ExampleModRegistries.EXAMPLE_FIREBALL_SPELL.getId();

            //boolean learned = EMSMagicApi.learnSpell(player, spellId);
            //if (learned) {
            //    ExaviorMagicSystem.LOGGER.info("Player {} learned spell: {}", player.getName().getString(), spellId);
            //}

            //EMSMagicApi.learnSpell(player, ExampleModRegistries.EXAMPLE_AREA_HEAL_SPELL.getId());
            //EMSMagicApi.learnSpell(player, ExampleModRegistries.EXAMPLE_FIREBALL_MINIGUN_SPELL.getId());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            int manaValue = player.getData(EMSDataAttachments.MANA_VALUE);
            //System.out.println("Mana Value: " + manaValue);

            int maxManaValue = player.getData(EMSDataAttachments.MAX_MANA_VALUE);
            //System.out.println("Max Mana Value: " + maxManaValue);
        }
    }

    @SubscribeEvent
    public static void onPlayerPostTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            handleManaRegen(player);

            handleCasting(player);
        }
    }

    private static void handleManaRegen(ServerPlayer player) {
        if (!EMSConfig.SERVER.passiveManaRegen.get()) {
            return;
        }

        int maxMana = player.getData(EMSDataAttachments.MAX_MANA_VALUE.get());
        int currentMana = player.getData(EMSDataAttachments.MANA_VALUE.get());
        if (currentMana >= maxMana) {
            return;
        }

        long gameTime = player.level().getGameTime();
        long regenReadyTime = player.getData(EMSDataAttachments.MANA_REGEN_COOLDOWN_UNTIL.get());
        if (gameTime < regenReadyTime) {
            return;
        }

        int manaTickRegen = EMSConfig.SERVER.manaTickRegen.get();
        if (manaTickRegen <= 0) {
            return;
        }

        if (gameTime % manaTickRegen == 0) {
            player.setData(EMSDataAttachments.MANA_VALUE.get(), currentMana + 1);
        }
    }

    private static void handleCasting(ServerPlayer player) {
        CastingState currentState = player.getData(EMSDataAttachments.CASTING_STATE.get());

        if (!currentState.isCasting()) {
            return;
        }

        ServerLevel level = player.serverLevel();
        long gameTime = level.getGameTime();

        Registry<Spell> spellRegistry = level.registryAccess().registryOrThrow(EMSRegistries.SPELL_REGISTRY_KEY);
        Spell spell = spellRegistry.get(currentState.spellId());

        if (spell == null) {
            player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);
            EMSMagicApi.stopArmPose(player);
            return;
        }

        long timeElapsed = gameTime - currentState.startTime();

        if (currentState.phase() == CastingPhase.CHARGING) {
            if (timeElapsed >= spell.getChargeTimeTicks()) {
                CastingState newState = new CastingState(currentState.spellId(), CastingPhase.CASTING, gameTime);
                player.setData(EMSDataAttachments.CASTING_STATE.get(), newState);
                EMSMagicApi.playArmPose(player, spell.getCastArmPose(), spell.getSpellArm());
            }

        } else if (currentState.phase() == CastingPhase.CASTING) {
            if (timeElapsed >= spell.getCastTimeTicks()) {
                if (!EMSMagicApi.canCastSpell(player, spell, currentState.spellId())) {
                    player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);
                    EMSMagicApi.stopArmPose(player);
                    return;
                }
                EMSMagicApi.applySpellCosts(player, spell, currentState.spellId());
                boolean canStartActive = spell.getActiveTimeTicks() > 0 &&
                        player.getData(EMSDataAttachments.IS_CAST_KEY_HELD.get());
                if (canStartActive) {
                    CastingState newState = new CastingState(currentState.spellId(), CastingPhase.ACTIVE, gameTime);
                    player.setData(EMSDataAttachments.CASTING_STATE.get(), newState);
                    spell.cast(level, player);
                    EMSMagicApi.playArmPose(player, spell.getActiveArmPose(), spell.getSpellArm());
                } else {
                    spell.cast(level, player);
                    player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);
                    EMSMagicApi.stopArmPose(player);
                }
            }

        } else if (currentState.phase() == CastingPhase.ACTIVE) {
            boolean isTimeUp = timeElapsed >= spell.getActiveTimeTicks();
            boolean isKeyReleased = !player.getData(EMSDataAttachments.IS_CAST_KEY_HELD.get());

            if (isTimeUp || isKeyReleased) {
                player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);
                EMSMagicApi.stopArmPose(player);
                return;
            }
            if (EMSMagicApi.hasEnoughManaForActiveTick(player, spell)) {
                spell.activeTick(level, player);
            } else {
                player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);
                EMSMagicApi.stopArmPose(player);
            }
        }
    }
}
