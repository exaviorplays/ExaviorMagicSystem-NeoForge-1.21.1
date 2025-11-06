package net.exavior.exmagicsys.network;

import net.exavior.exmagicsys.network.client.EMSClientPayloadHandler;
import net.exavior.exmagicsys.network.client.toserverpackets.*;
import net.exavior.exmagicsys.network.server.EMSServerPayloadHandler;
import net.exavior.exmagicsys.network.server.toclientpackets.ClientboundClearArmPosePacket;
import net.exavior.exmagicsys.network.server.toclientpackets.ClientboundSetArmPosePacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class EMSNetworkHandler {
    public static void register(final PayloadRegistrar registrar){
        //Server to Client
        /*registrar.playToClient(STCAddWallJumpRegenPacket.TYPE, STCAddWallJumpRegenPacket.STREAM_CODEC,
                DozedClientPayloadHandler.getInstance()::handleAddWallJumpRegenToClient);*/
        registrar.playToClient(
                ClientboundSetArmPosePacket.TYPE,
                ClientboundSetArmPosePacket.STREAM_CODEC,
                EMSClientPayloadHandler.getInstance()::handleSetArmPose
        );
        registrar.playToClient(
                ClientboundClearArmPosePacket.TYPE,
                ClientboundClearArmPosePacket.STREAM_CODEC,
                EMSClientPayloadHandler.getInstance()::handleClearArmPose
        );

        //Client to Server
        /*registrar.playToServer(ServerboundCastSpellPacket.TYPE, ServerboundCastSpellPacket.STREAM_CODEC,
                EMSServerPayloadHandler.getInstance()::handleCastSpellPacket);*/

        registrar.playToServer(ServerboundStartCastPacket.TYPE, ServerboundStartCastPacket.STREAM_CODEC,
                EMSServerPayloadHandler.getInstance()::handleStartCastPacket);
        registrar.playToServer(ServerboundReleaseCastKeyPacket.TYPE, ServerboundReleaseCastKeyPacket.STREAM_CODEC,
                EMSServerPayloadHandler.getInstance()::handleReleaseCastKeyPacket);

        registrar.playToServer(ServerboundCycleSpellPacket.TYPE, ServerboundCycleSpellPacket.STREAM_CODEC, EMSServerPayloadHandler.getInstance()::handleCycleSpellPacket);
        registrar.playToServer(ServerboundSetEquippedSpellPacket.TYPE, ServerboundSetEquippedSpellPacket.STREAM_CODEC, EMSServerPayloadHandler.getInstance()::handleSetEquippedSpellPacket);

    }
}
