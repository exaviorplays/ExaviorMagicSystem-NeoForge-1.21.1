package net.exavior.exmagicsys.examplemod.spells;

import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.api.spell.SpellProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class FireballMinigunSpell extends Spell {
    public FireballMinigunSpell(SpellProperties properties) {
        super(properties);
    }

    @Override
    public void cast(ServerLevel level, ServerPlayer player, @Nullable ItemStack stack) {
        if (stack != null) {
            player.getCooldowns().addCooldown(stack.getItem(), this.getCooldownTicks());
        }
    }

    @Override
    public void activeTick(ServerLevel level, ServerPlayer player, @Nullable ItemStack stack) {
        if (level.getGameTime() % 2 == 0) {
            if (EMSMagicApi.consumeActiveTickMana(player, this)) {
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

                level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.4F, 1.2F);

                super.activeTick(level, player, stack);
            }
        }
    }
}
