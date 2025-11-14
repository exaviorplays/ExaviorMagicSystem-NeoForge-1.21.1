package net.exavior.exmagicsys.network.server;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.spell.CastSource;
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

    public void handleStartCastPacket(ServerStartCastPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            EMSMagicApi.startCasting(context.player(), packet.spellId(), CastSource.KEYBIND, null, true);
        });
    }

    public void handleReleaseCastKeyPacket(ServerReleaseCastKeyPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            EMSMagicApi.releaseCasting(context.player(), CastSource.KEYBIND);
        });
    }

    public void handleCycleSpellPacket(ServerCycleSpellPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                EMSMagicApi.cycleActiveSpell(context.player());
            }
        });
    }

    public void handleSetEquippedSpellPacket(ServerSetEquippedSpellPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                EMSMagicApi.setEquippedSpell(context.player(), packet.slot(), packet.spellId());
            }
        });
    }
}