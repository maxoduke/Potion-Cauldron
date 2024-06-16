package dev.maxoduke.mods.potioncauldron.block;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public class CauldronInteractionInjector
{
    // I do not like this at all, but vanilla Sponge Mixin doesn't support interface injectors.
    // Times like these make me appreciate fabric loom T_T
    public static void injectIntoEmptyPotionInteraction()
    {
        var emptyPotionInteraction = CauldronInteraction.EMPTY.map().get(Items.POTION);
        CauldronInteraction.EMPTY.map().remove(Items.POTION);

        CauldronInteraction.EMPTY.map().put(Items.POTION, (blockState, level, blockPos, player, interactionHand, itemStack) ->
        {
            PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
            if (potionContents != null && !potionContents.is(Potions.WATER))
                return PotionCauldronBlockInteraction.fillEmptyCauldronWithPotion(blockState, level, blockPos, player, interactionHand, itemStack);

            return emptyPotionInteraction.interact(blockState, level, blockPos, player, interactionHand, itemStack);
        });
    }
}
