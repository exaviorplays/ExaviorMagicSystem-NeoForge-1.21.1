package net.exavior.exmagicsys.network.server;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.data.CastingPhase;
import net.exavior.exmagicsys.data.CastingState;
import net.exavior.exmagicsys.network.client.toserverpackets.*;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EMSServerPayloadHandler {
    private static final EMSServerPayloadHandler INSTANCE = new EMSServerPayloadHandler();

    public static EMSServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    private void fireInstantSpell(ServerPlayer player, ServerLevel level, Spell spell, ResourceLocation spellId) {
        if (EMSMagicApi.canCastSpell(player, spell, spellId)) {
            spell.cast(level, player);
            EMSMagicApi.applySpellCosts(player, spell, spellId);
        }
    }

    public void handleStartCastPacket(ServerStartCastPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {

                player.setData(EMSDataAttachments.IS_CAST_KEY_HELD.get(), true);

                CastingState currentState = player.getData(EMSDataAttachments.CASTING_STATE.get());
                if (currentState.isCasting()) {
                    return;
                }

                Registry<Spell> spellRegistry = level.registryAccess().registryOrThrow(EMSRegistries.SPELL_REGISTRY_KEY);
                Spell spell = spellRegistry.get(packet.spellId());

                if (spell == null) {
                    ExaviorMagicSystem.LOGGER.warn("Player {} tried to cast unknown spell: {}", player.getName().getString(), packet.spellId());
                    return;
                }

                if (EMSMagicApi.knowsSpell(player, packet.spellId()) &&
                        EMSMagicApi.canCastSpell(player, spell, packet.spellId())) {

                    if (spell.getChargeTimeTicks() > 0) {
                        CastingState newState = new CastingState(packet.spellId(), CastingPhase.CHARGING, level.getGameTime());
                        player.setData(EMSDataAttachments.CASTING_STATE.get(), newState);

                        EMSMagicApi.playArmPose(player, spell.getChargeArmPose(), spell.getSpellArm());

                    } else if (spell.getCastTimeTicks() > 0) {
                        CastingState newState = new CastingState(packet.spellId(), CastingPhase.CASTING, level.getGameTime());
                        player.setData(EMSDataAttachments.CASTING_STATE.get(), newState);

                        EMSMagicApi.playArmPose(player, spell.getChargeArmPose(), spell.getSpellArm());

                    } else {
                        fireInstantSpell(player, level, spell, packet.spellId());
                    }
                }
            }
        });
    }

    public void handleReleaseCastKeyPacket(ServerReleaseCastKeyPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {

                player.setData(EMSDataAttachments.IS_CAST_KEY_HELD.get(), false);

                CastingState currentState = player.getData(EMSDataAttachments.CASTING_STATE.get());

                if (currentState.isCasting() && currentState.phase() == CastingPhase.CHARGING) {
                    player.setData(EMSDataAttachments.CASTING_STATE.get(), CastingState.NONE);

                    EMSMagicApi.stopArmPose(player);
                }
            }
        });
    }

    public void handleCycleSpellPacket(ServerCycleSpellPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                EMSMagicApi.cycleActiveSpell(player);
            }
        });
    }

    public void handleSetEquippedSpellPacket(ServerSetEquippedSpellPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                EMSMagicApi.setEquippedSpell(player, packet.slot(), packet.spellId());
            }
        });
    }
}