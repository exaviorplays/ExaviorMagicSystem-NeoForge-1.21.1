package net.exavior.exmagicsys;

import net.exavior.exmagicsys.api.event.OpenSpellGuiEvent;
import net.exavior.exmagicsys.api.hud.ManaBarOverlay;
import net.exavior.exmagicsys.gui.screen.SpellSelectionScreen;
import net.exavior.exmagicsys.keymap.EMSClientKeyMaps;
import net.exavior.exmagicsys.network.client.toserverpackets.ServerCycleSpellPacket;
import net.exavior.exmagicsys.network.client.toserverpackets.ServerReleaseCastKeyPacket;
import net.exavior.exmagicsys.network.client.toserverpackets.ServerStartCastPacket;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.List;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = ExaviorMagicSystem.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = ExaviorMagicSystem.MODID, value = Dist.CLIENT)
public class ExaviorMagicSystemClient {
    public ExaviorMagicSystemClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        ExaviorMagicSystem.LOGGER.info("HELLO FROM CLIENT SETUP");
        ExaviorMagicSystem.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(EMSClientKeyMaps.CAST_SPELL_KEY);
        event.register(EMSClientKeyMaps.OPEN_SPELL_GUI_KEY);
        event.register(EMSClientKeyMaps.CYCLE_SPELL_KEY);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.FOOD_LEVEL,
                ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "mana_bar"),
                ManaBarOverlay.OVERLAY
        );
    }

    @EventBusSubscriber(modid = ExaviorMagicSystem.MODID, value = Dist.CLIENT)
    public static class InputEvents {

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            if (EMSClientKeyMaps.OPEN_SPELL_GUI_KEY.consumeClick()) {
                if (mc.screen != null) {
                    mc.setScreen(null);
                } else {
                    // Fire a cancellable event to allow other mods to override the GUI
                    boolean wasCanceled = NeoForge.EVENT_BUS.post(
                            new OpenSpellGuiEvent(mc.player)
                    ).isCanceled();

                    // Only open the screen if the event was *not* canceled
                    if (!wasCanceled) {
                        mc.setScreen(new SpellSelectionScreen());
                    }
                }
                return;
            }

            if (EMSClientKeyMaps.CYCLE_SPELL_KEY.consumeClick()) {
                if (mc.screen == null) {
                    PacketDistributor.sendToServer(new ServerCycleSpellPacket());
                }
                return;
            }

            if (event.getKey() == EMSClientKeyMaps.CAST_SPELL_KEY.getKey().getValue()) {

                if (mc.screen != null) {
                    return;
                }

                int activeSlot = mc.player.getData(EMSDataAttachments.ACTIVE_SPELL_SLOT.get());
                List<ResourceLocation> spells = mc.player.getData(EMSDataAttachments.EQUIPPED_SPELLS.get());
                ResourceLocation spellToCast = null;

                if (spells != null && spells.size() == 4) {
                    spellToCast = spells.get(activeSlot);
                }

                if (spellToCast == null) {
                    if (event.getAction() == GLFW.GLFW_PRESS) {
                        mc.player.displayClientMessage(Component.translatable("exmagicsys.feedback.no_spell_selected"), true);
                    }
                    return;
                }

                if (event.getAction() == GLFW.GLFW_PRESS) {
                    PacketDistributor.sendToServer(new ServerStartCastPacket(spellToCast));
                } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                    PacketDistributor.sendToServer(new ServerReleaseCastKeyPacket());
                }
            }
        }
    }
}
