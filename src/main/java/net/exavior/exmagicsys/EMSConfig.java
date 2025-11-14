package net.exavior.exmagicsys;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class EMSConfig {

    public static final CLIENT CLIENT;
    public static final SERVER SERVER;
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        Pair<CLIENT, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(CLIENT::new);

        CLIENT = pair.getLeft();
        CLIENT_SPEC = pair.getRight();
    }

    static {
        Pair<SERVER, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(SERVER::new);

        SERVER = pair.getLeft();
        SERVER_SPEC = pair.getRight();
    }

    public static class CLIENT {
        public final ModConfigSpec.IntValue manaBarColor;

        CLIENT(ModConfigSpec.Builder builder) {
            builder.comment("Client Configuration Settings")
                    .push("client");
            manaBarColor = builder
                    .comment("The Color of the Mana Bar, in ARGB hex format. Default: 16755286 (0xFF0055AA) (Blue)")
                    .defineInRange("mana_bar_color", 0xFF0055AA, Integer.MIN_VALUE, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class SERVER {
        public final ModConfigSpec.BooleanValue passiveManaRegen;
        public final ModConfigSpec.BooleanValue manaDecreaseCauseCooldown;
        public final ModConfigSpec.IntValue manaRegenCooldown;
        public final ModConfigSpec.IntValue manaTickRegen;
        public final ModConfigSpec.IntValue combatCooldown;

        SERVER(ModConfigSpec.Builder builder) {
            builder.comment("Server Configuration Settings")
                    .push("server");

            passiveManaRegen = builder
                    .comment(" Whether if mana will regen passively.\n Default: false")
                    .define("passive_mana_regen", false);

            manaDecreaseCauseCooldown = builder
                    .comment("\n Whether if any time the mana decreases it causes the cooldown.\n If it is false, cooldown is only caused when spells are cast.\n Default: false")
                    .define("mana_decrease_cause_cooldown", false);

            manaRegenCooldown = builder
                    .comment("\n How long the cooldown is until mana begins to regen again. 20 ticks = 1 second.")
                    .defineInRange("mana_regen_cooldown", 100, 0, 1200);

            manaTickRegen = builder
                    .comment("\n How many ticks per mana. 20 ticks = 1 second.")
                    .defineInRange("mana_tick_regen", 10, 0, 1200);

            combatCooldown = builder
                    .comment("\n How long the cooldown is when the player enters combat. 20 ticks = 1 second.")
                    .defineInRange("combat_cooldown", 1200, 0, 3600);

            builder.pop();
        }
    }

}
