package net.exavior.exmagicsys.api.spell;

import net.minecraft.network.chat.Component;

/**
 * Data-holding class for spell classifications (e.g., Fire, Water, Arcane).
 * These are registered into a custom registry.
 *
 * @param description The translatable name of the classification (e.g., "Fire").
 */
public record SpellClassification(Component description) {

}