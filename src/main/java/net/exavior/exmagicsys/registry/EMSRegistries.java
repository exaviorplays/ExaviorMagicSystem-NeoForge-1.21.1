package net.exavior.exmagicsys.registry;

import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.api.spell.SpellClassification;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = ExaviorMagicSystem.MODID)
public class EMSRegistries {

    public static final ResourceKey<Registry<Spell>> SPELL_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "spells"));

    public static final ResourceKey<Registry<SpellClassification>> CLASSIFICATION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "spell_classifications"));

    public static final DeferredRegister<Spell> SPELLS =
            DeferredRegister.create(SPELL_REGISTRY_KEY, ExaviorMagicSystem.MODID);

    public static final DeferredRegister<SpellClassification> SPELL_CLASSIFICATIONS =
            DeferredRegister.create(CLASSIFICATION_REGISTRY_KEY, ExaviorMagicSystem.MODID);


    public static final DeferredHolder<SpellClassification, SpellClassification> CLASSIFICATION_NONE =
            SPELL_CLASSIFICATIONS.register("none", () ->
                    new SpellClassification(Component.translatable("exmagicsys.classification.none"))
            );

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        event.create(new RegistryBuilder<>(SPELL_REGISTRY_KEY)
                .maxId(Integer.MAX_VALUE - 1)
                .sync(true)
        );

        event.create(new RegistryBuilder<>(CLASSIFICATION_REGISTRY_KEY)
                .maxId(Integer.MAX_VALUE - 1)
                .sync(true)
                .defaultKey(CLASSIFICATION_NONE.getId())
        );
    }

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
        SPELL_CLASSIFICATIONS.register(eventBus);
    }
}