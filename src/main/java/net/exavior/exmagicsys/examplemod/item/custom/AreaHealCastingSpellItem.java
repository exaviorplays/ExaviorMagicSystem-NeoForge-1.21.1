package net.exavior.exmagicsys.examplemod.item.custom;

import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.spell.CastSource;
import net.exavior.exmagicsys.examplemod.ExampleModRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class AreaHealCastingSpellItem extends Item {
    public AreaHealCastingSpellItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide) {
            ResourceLocation spellId = ExampleModRegistries.EXAMPLE_AREA_HEAL_SPELL.getId();

            EMSMagicApi.startCasting(player, spellId, CastSource.ITEM, usedHand, false);

            player.startUsingItem(usedHand);

            return InteractionResultHolder.consume(stack);
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (!level.isClientSide() && entity instanceof Player player) {
            EMSMagicApi.releaseCasting(player);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }
}
