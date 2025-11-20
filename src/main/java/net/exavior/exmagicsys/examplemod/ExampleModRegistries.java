package net.exavior.exmagicsys.examplemod;

import net.exavior.exmagicsys.api.client.SpellAnimations;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.api.spell.SpellArm;
import net.exavior.exmagicsys.api.spell.SpellClassification;
import net.exavior.exmagicsys.api.spell.SpellProperties;
import net.exavior.exmagicsys.examplemod.item.custom.AreaHealCastingSpellItem;
import net.exavior.exmagicsys.examplemod.item.custom.FireballCastingSpellItem;
import net.exavior.exmagicsys.examplemod.spells.FireballMinigunSpell;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.exavior.exmagicsys.examplemod.spells.AreaHealSpell;
import net.exavior.exmagicsys.examplemod.spells.FireballSpell;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/***
 * Example Registry for creating new Spells and Spell Classifications
 */
public class ExampleModRegistries {

    public static final String EXAMPLE_MODID = "examplemod";

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(EXAMPLE_MODID);

    public static final DeferredRegister<Spell> SPELLS =
            DeferredRegister.create(EMSRegistries.SPELL_REGISTRY_KEY, EXAMPLE_MODID);

    public static final DeferredRegister<SpellClassification> SPELL_CLASSIFICATIONS =
            DeferredRegister.create(EMSRegistries.CLASSIFICATION_REGISTRY_KEY, EXAMPLE_MODID);


    public static final DeferredItem<Item> FIREBALL_CASTING_SPELL_ITEM = ITEMS.register("fireball_casting_spell_item",
            () -> new FireballCastingSpellItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> AREA_HEAL_CASTING_SPELL_ITEM = ITEMS.register("area_heal_casting_spell_item",
            () -> new AreaHealCastingSpellItem(new Item.Properties().stacksTo(1)));


    // --- Fire Example Classification ---
    public static final DeferredHolder<SpellClassification, SpellClassification> EXAMPLE_CLASSIFICATION_FIRE =
            SPELL_CLASSIFICATIONS.register("example_fire", () ->
                    new SpellClassification(Component.translatable("exmagicsys.classification.example_fire"))
            );

    // --- Divine Example Classification ---
    public static final DeferredHolder<SpellClassification, SpellClassification> EXAMPLE_CLASSIFICATION_DIVINE =
            SPELL_CLASSIFICATIONS.register("example_divine", () ->
                    new SpellClassification(Component.translatable("exmagicsys.classification.example_divine"))
            );

    // --- Fireball Spell ---
    public static final DeferredHolder<Spell, Spell> EXAMPLE_FIREBALL_SPELL =
            SPELLS.register("example_fireball", () ->
                    new FireballSpell(new SpellProperties()
                            .classification(EXAMPLE_CLASSIFICATION_FIRE)
                            .manaCost(10)
                            .cooldownTicks(40) // 2 seconds cooldown
                            .castTimeTicks(10) // Casts after 0.5 seconds
                            .chargeTimeTicks(0) // No charge
                            .spellArm(SpellArm.MAIN_HAND)
                            .castAnim(SpellAnimations.SWIPE_HORIZONTAL)
                    )
            );

    // --- Fireball Minigun Spell ---
    public static final DeferredHolder<Spell, Spell> EXAMPLE_FIREBALL_MINIGUN_SPELL =
            SPELLS.register("example_fireball_minigun", () ->
                    new FireballMinigunSpell(new SpellProperties()
                            .classification(EXAMPLE_CLASSIFICATION_FIRE)
                            .manaCost(20)
                            .cooldownTicks(400)
                            .chargeTimeTicks(100)
                            .activeTimeTicks(300)
                            .manaCostPerActiveTick(3)
                            .spellArm(SpellArm.MAIN_HAND)
                            .chargeAnim(SpellAnimations.TWIRL)
                            .activeAnim(SpellAnimations.THRUST_HOLD)
                    )
            );

    // --- Area Heal Spell ---
    public static final DeferredHolder<Spell, Spell> EXAMPLE_AREA_HEAL_SPELL =
            SPELLS.register("example_area_heal", () ->
                    new AreaHealSpell(new SpellProperties()
                            .classification(EXAMPLE_CLASSIFICATION_DIVINE)
                            .manaCost(10)
                            .cooldownTicks(200) //10 seconds cooldown
                            .chargeTimeTicks(40) // 2 seconds to charge
                            .castTimeTicks(0)    // Instantly casts after charge
                            .activeTimeTicks(200) // 10 seconds of active time
                            .manaCostPerActiveTick(5) // Consumed every time MagicApi.consumeActiveTickMana() is called
                            .spellArm(SpellArm.BOTH) // Use both arms
                            .chargeAnim(SpellAnimations.TWIRL)
                            .activeAnim(SpellAnimations.BLOCK)
                    )
            );


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        SPELLS.register(eventBus);
        SPELL_CLASSIFICATIONS.register(eventBus);
    }
}
