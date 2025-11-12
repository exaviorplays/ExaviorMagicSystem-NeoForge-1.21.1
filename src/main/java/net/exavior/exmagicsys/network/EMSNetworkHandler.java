package net.exavior.exmagicsys.network;

import net.exavior.exmagicsys.network.client.EMSClientPayloadHandler;
import net.exavior.exmagicsys.network.client.toserverpackets.*;
import net.exavior.exmagicsys.network.server.EMSServerPayloadHandler;
import net.exavior.exmagicsys.network.server.toclientpackets.ClientClearArmPosePacket;
import net.exavior.exmagicsys.network.server.toclientpackets.ClientSetArmPosePacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class EMSNetworkHandler {
    public static void register(final PayloadRegistrar registrar){
        //Server to Client
        /*registrar.playToClient(STCAddWallJumpRegenPacket.TYPE, STCAddWallJumpRegenPacket.STREAM_CODEC,
                DozedClientPayloadHandler.getInstance()::handleAddWallJumpRegenToClient);*/
        registrar.playToClient(
                ClientSetArmPosePacket.TYPE,
                ClientSetArmPosePacket.STREAM_CODEC,
                EMSClientPayloadHandler.getInstance()::handleSetArmPose
        );

        registrar.playToClient(
                ClientClearArmPosePacket.TYPE,
                ClientClearArmPosePacket.STREAM_CODEC,
                EMSClientPayloadHandler.getInstance()::handleClearArmPose
        );

        //Client to Server
        /*registrar.playToServer(ServerboundCastSpellPacket.TYPE, ServerboundCastSpellPacket.STREAM_CODEC,
                EMSServerPayloadHandler.getInstance()::handleCastSpellPacket);*/

        registrar.playToServer(ServerStartCastPacket.TYPE,
                ServerStartCastPacket.STREAM_CODEC,
                EMSServerPayloadHandler.getInstance()::handleStartCastPacket
        );

        registrar.playToServer(ServerReleaseCastKeyPacket.TYPE,
                ServerReleaseCastKeyPacket.STREAM_CODEC,
                EMSServerPayloadHandler.getInstance()::handleReleaseCastKeyPacket
        );

        registrar.playToServer(ServerCycleSpellPacket.TYPE, ServerCycleSpellPacket.STREAM_CODEC, EMSServerPayloadHandler.getInstance()::handleCycleSpellPacket);
        registrar.playToServer(ServerSetEquippedSpellPacket.TYPE, ServerSetEquippedSpellPacket.STREAM_CODEC, EMSServerPayloadHandler.getInstance()::handleSetEquippedSpellPacket);

    }
}
