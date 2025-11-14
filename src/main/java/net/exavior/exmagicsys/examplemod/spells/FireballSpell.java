package net.exavior.exmagicsys.examplemod.spells;

import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.api.spell.SpellProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class FireballSpell extends Spell {

    public FireballSpell(SpellProperties properties) {
        super(properties);
    }

    /**
     * The cast() method is called once when the cast time is finished.
     */
    @Override
    public void cast(ServerLevel level, ServerPlayer player, @Nullable ItemStack stack) {
        Vec3 look = player.getLookAngle();

        SmallFireball fireball = new SmallFireball(level,
                player.getX() + look.x,
                player.getEyeY() - 1.0,
                player.getZ() + look.z,
                player.getLookAngle().scale(2.0));

        fireball.setOwner(player);

        double power = 1.5;
        fireball.setDeltaMovement(look.x * power, look.y * power, look.z * power);

        level.addFreshEntity(fireball);

        if (stack != null) {
            player.getCooldowns().addCooldown(stack.getItem(), this.getCooldownTicks());
        }
    }
}