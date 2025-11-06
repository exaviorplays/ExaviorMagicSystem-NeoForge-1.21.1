package net.exavior.exmagicsys;

import net.exavior.exmagicsys.network.EMSNetworkHandler;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.exavior.exmagicsys.examplemod.ExampleModRegistries;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExaviorMagicSystem.MODID)
public class ExaviorMagicSystem {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "exmagicsys";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();



    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ExaviorMagicSystem(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloadHandler);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExaviorMagicSystem) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        EMSDataAttachments.register(modEventBus);
        EMSRegistries.register(modEventBus);

        // TODO: Register the spell registries like this
        // It's not to do, but just want people to see this if need be
        //ExampleModRegistries.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.CLIENT, EMSConfig.CLIENT_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, EMSConfig.SERVER_SPEC);
    }

    private void registerPayloadHandler(final RegisterPayloadHandlersEvent evt) {
        EMSNetworkHandler.register(evt.registrar("1.0"));
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {

        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
