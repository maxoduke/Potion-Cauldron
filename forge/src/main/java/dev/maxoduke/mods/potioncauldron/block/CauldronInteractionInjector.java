package dev.maxoduke.mods.potioncauldron.block;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public class CauldronInteractionInjector
{
    // I do not like this at all, but vanilla Sponge Mixin doesn't support interface injectors.
    // Times like these make me appreciate fabric loom T_T
    public static void injectIntoEmptyPotionInteraction()
    {
        var emptyPotionInteraction = CauldronInteraction.EMPTY.get(Items.POTION);
        CauldronInteraction.EMPTY.remove(Items.POTION);

        CauldronInteraction.EMPTY.put(Items.POTION, (blockState, level, blockPos, player, interactionHand, itemStack) ->
        {
            if (PotionUtils.getPotion(itemStack) != Potions.WATER)
                return PotionCauldronBlockInteraction.fillEmptyCauldronWithPotion(blockState, level, blockPos, player, interactionHand, itemStack);

            return emptyPotionInteraction.interact(blockState, level, blockPos, player, interactionHand, itemStack);
        });
    }
}
