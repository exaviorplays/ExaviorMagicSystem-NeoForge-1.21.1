package net.exavior.exmagicsys.examplemod.item.custom;

import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.spell.CastSource;
import net.exavior.exmagicsys.examplemod.ExampleModRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FireballCastingSpellItem extends Item {
    public FireballCastingSpellItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide) {
            ResourceLocation spellId = ExampleModRegistries.EXAMPLE_FIREBALL_SPELL.getId();

            EMSMagicApi.startCasting(player, spellId, CastSource.ITEM, usedHand, false);

            return InteractionResultHolder.success(stack);
        }

        return super.use(level, player, usedHand);
    }
}
