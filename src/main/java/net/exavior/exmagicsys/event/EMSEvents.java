package net.exavior.exmagicsys.event;

import net.exavior.exmagicsys.EMSConfig;
import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.event.OpenSpellGuiEvent;
import net.exavior.exmagicsys.api.spell.CastSource;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.command.SpellCommand;
import net.exavior.exmagicsys.data.CastingPhase;
import net.exavior.exmagicsys.data.CastingState;
import net.exavior.exmagicsys.examplemod.ExampleModRegistries;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
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
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SpellCommand.register(event.getDispatcher());
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
    public static void onCombatCooldown(OpenSpellGuiEvent event) {
        if (event.getPlayer() instanceof Player player) {
            if (player.hasInfiniteMaterials()) {
                return;
            }

            long gameTime = player.level().getGameTime();

            long cooldownFinished = player.getData(EMSDataAttachments.COMBAT_COOLDOWN_UNTIL.get());
            if (gameTime < cooldownFinished) {
                long ticksRemaining = cooldownFinished - gameTime;
                player.displayClientMessage(Component.translatable("exmagicsys.feedback.on_combat_cooldown", String.format("%.1f", ticksRemaining / 20.0f)), true);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onCombatStart(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() != null && event.getEntity() instanceof ServerPlayer player) {
            EMSMagicApi.triggerCombatCooldown(player);
        }
        else if (event.getSource().getDirectEntity() instanceof ServerPlayer player) {
            EMSMagicApi.triggerCombatCooldown(player);
        }
        else if (event.getSource().getEntity() instanceof ServerPlayer player) {
            EMSMagicApi.triggerCombatCooldown(player);
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
                CastingState newState = new CastingState(currentState.spellId(), CastingPhase.CASTING, gameTime, currentState.source(), currentState.hand());
                player.setData(EMSDataAttachments.CASTING_STATE.get(), newState);
                EMSMagicApi.playArmPose(player, spell.getCastAnim(), spell.getSpellArm());
            } else {

            }
            spell.chargeTick(level, player);

        } else if (currentState.phase() == CastingPhase.CASTING) {
            if (timeElapsed >= spell.getCastTimeTicks()) {
                if (!EMSMagicApi.canCastSpell(player, spell, currentState.spellId())) {
                    player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);
                    EMSMagicApi.stopArmPose(player);
                    return;
                }

                boolean isHolding = false;
                if (currentState.source() == CastSource.KEYBIND) {
                    isHolding = player.getData(EMSDataAttachments.IS_CAST_KEY_HELD.get());
                } else if (currentState.source() == CastSource.ITEM) {
                    isHolding = player.isUsingItem() && player.getUsedItemHand() == currentState.hand();
                }

                EMSMagicApi.applySpellCosts(player, spell, currentState.spellId());
                boolean canStartActive = spell.getActiveTimeTicks() > 0 && isHolding;

                if (canStartActive) {
                    CastingState newState = new CastingState(currentState.spellId(), CastingPhase.ACTIVE, gameTime, currentState.source(), currentState.hand());
                    player.setData(EMSDataAttachments.CASTING_STATE.get(), newState);
                    if(currentState.source() == CastSource.ITEM) {
                        spell.cast(level, player, player.getItemInHand(currentState.hand()));
                    } else {
                        spell.cast(level, player, null);
                    }
                    EMSMagicApi.playArmPose(player, spell.getActiveAnim(), spell.getSpellArm());
                } else {
                    if(currentState.source() == CastSource.ITEM) {
                        spell.cast(level, player, player.getItemInHand(currentState.hand()));
                    } else {
                        spell.cast(level, player, null);
                    }
                    player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);
                    EMSMagicApi.stopArmPose(player);
                }
            }
            spell.castTick(level, player);

        } else if (currentState.phase() == CastingPhase.ACTIVE) {
            boolean isReleased = false;
            if (currentState.source() == CastSource.KEYBIND) {
                isReleased = !player.getData(EMSDataAttachments.IS_CAST_KEY_HELD.get());
            } else if (currentState.source() == CastSource.ITEM) {
                isReleased = !player.isUsingItem() || player.getUsedItemHand() != currentState.hand();
            }

            boolean isTimeUp = timeElapsed >= spell.getActiveTimeTicks();

            if (isTimeUp || isReleased) {
                player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);
                EMSMagicApi.stopArmPose(player);
                return;
            }

            if (EMSMagicApi.hasEnoughManaForActiveTick(player, spell)) {
                if(currentState.source() == CastSource.ITEM) {
                    spell.activeTick(level, player, player.getItemInHand(currentState.hand()));
                } else {
                    spell.activeTick(level, player, null);
                }
            } else {
                player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);
                EMSMagicApi.stopArmPose(player);
            }
        }
    }
}
