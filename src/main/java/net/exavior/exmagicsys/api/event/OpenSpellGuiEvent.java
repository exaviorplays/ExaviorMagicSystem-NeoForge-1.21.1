package net.exavior.exmagicsys.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired on the client when the "Open Spell Menu" keybind is pressed.
 * If canceled, the default spell selection screen will not be opened.
 * This allows other mods to provide their own screen.
 */
public class OpenSpellGuiEvent extends Event implements ICancellableEvent {
    private final Player player;

    public OpenSpellGuiEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}