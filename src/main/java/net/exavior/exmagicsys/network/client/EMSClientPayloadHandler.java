    package net.exavior.exmagicsys.network.client;

    import net.exavior.exmagicsys.ExaviorMagicSystem;
    import net.exavior.exmagicsys.network.server.toclientpackets.ClientboundClearArmPosePacket;
    import net.exavior.exmagicsys.network.server.toclientpackets.ClientboundSetArmPosePacket;
    import net.exavior.exmagicsys.registry.EMSDataAttachments;
    import net.minecraft.client.Minecraft;
    import net.minecraft.resources.ResourceLocation;
    import net.minecraft.world.entity.player.Player;
    import net.neoforged.neoforge.network.handling.IPayloadContext;


    public class EMSClientPayloadHandler {

        private static final EMSClientPayloadHandler INSTANCE = new EMSClientPayloadHandler();

        public static EMSClientPayloadHandler getInstance() {
            return INSTANCE;
        }

        public void handleSetArmPose(ClientboundSetArmPosePacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    player.setData(EMSDataAttachments.CLIENT_SPELL_ARM_POSE.get(), packet.pose());
                    player.setData(EMSDataAttachments.CLIENT_SPELL_ARM.get(), packet.arm());
                }
            });
        }

        public void handleClearArmPose(ClientboundClearArmPosePacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    player.removeData(EMSDataAttachments.CLIENT_SPELL_ARM_POSE.get());
                    player.removeData(EMSDataAttachments.CLIENT_SPELL_ARM.get());
                }
            });
        }


        private static void handle(final IPayloadContext ctx, Runnable task) {
            ctx.enqueueWork(task)
                    .exceptionally(e -> {
                        // If you want to disconnect or log, uncomment:
                        // ctx.disconnect(Component.translatable("dozed.networking.failed", e.getMessage()));
                        ExaviorMagicSystem.LOGGER.error("Failed to handle packet: ", e);
                        return null;
                    });
        }



        /*public void handleToggleDash(final SPacketSyncToggleDash data, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                Player player = ctx.player();
                // ONLY modify the Dash state.
                player.getData(DozedAttachments.DASH_ENABLED).isToggled = data.toggled();

                // If the player is actively turning Dash ON, then turn Skip off.
                if (data.toggled()) {
                    player.getData(DozedAttachments.SKIP_ENABLED).isToggled = false;
                }
            });
        }*/
    }
