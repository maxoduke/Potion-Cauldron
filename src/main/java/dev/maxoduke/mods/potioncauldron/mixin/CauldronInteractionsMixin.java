package dev.maxoduke.mods.potioncauldron.mixin;

import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CauldronInteractions.class)
public class CauldronInteractionsMixin
{
    // lambda$bootStrap$0 is the lambda of CauldronInteractions.java > bootStrap() > EMPTY.put(Items.POTION, <lambda>)
    @SuppressWarnings({ "NameDoesntMatchTargetClass", "DataFlowIssue" })
    @Inject(method = "lambda$bootStrap$0", at = @At("HEAD"), cancellable = true)
    private static void handleEmptyCauldronAndPotionInteraction(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack, CallbackInfoReturnable<InteractionResult> cir)
    {
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        Holder<Potion> potion = potionContents.potion().isPresent() ? potionContents.potion().get() : null;

        if (potion != null && potion != Potions.WATER)
        {
            InteractionResult result = PotionCauldronBlockInteractions.fillEmptyCauldronWithPotion(blockState, level, blockPos, player, interactionHand, itemStack);
            cir.setReturnValue(result);
        }
    }
}
