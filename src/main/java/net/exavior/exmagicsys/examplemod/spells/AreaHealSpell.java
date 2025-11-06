package net.exavior.exmagicsys.examplemod.spells;

import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.api.spell.SpellProperties;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AreaHealSpell extends Spell {

    public AreaHealSpell(SpellProperties properties) {
        super(properties);
    }

    /**
     * The cast() method is called once when the cast time is finished,
     * just before the ACTIVE phase. We can use this to play an initial sound
     * or particle effect.
     */
    @Override
    public void cast(ServerLevel level, ServerPlayer player) {
        // We don't need to do anything here for this spell,
        // as the healing only happens during the activeTick.
    }

    /**
     * This is called every tick during the ACTIVE phase.
     */
    @Override
    public void activeTick(ServerLevel level, ServerPlayer player) {
        // Run this every 1.0 seconds (20 ticks)
        if (level.getGameTime() % 20 == 0) {

            // Try to consume the active tick mana.
            if (EMSMagicApi.consumeActiveTickMana(player, this)) {
                // --- Mana was successfully consumed, now run the heal logic ---

                AABB healingZone = player.getBoundingBox().inflate(5.0);

                List<LivingEntity> entitiesToHeal = level.getEntitiesOfClass(LivingEntity.class, healingZone, (entity) -> {
                    return entity.isAlive() && !(entity instanceof Monster);
                });

                for (LivingEntity entity : entitiesToHeal) {
                    entity.heal(2.0f); // Heal for 1 heart
                    double px = entity.getX();
                    double py = entity.getY() + 1.0;
                    double pz = entity.getZ();
                    level.sendParticles(ParticleTypes.HEART, px, py, pz, entitiesToHeal.size(), 0.5, 0.5, 0.5, 0.1);
                }
            }
            // The PlayerTickEvent will detect the lack of mana next tick and stop the spell.
        }
    }
}