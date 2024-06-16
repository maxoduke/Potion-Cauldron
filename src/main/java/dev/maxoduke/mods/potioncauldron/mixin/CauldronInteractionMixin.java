package dev.maxoduke.mods.potioncauldron.mixin;

import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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

@Mixin(CauldronInteraction.class)
public interface CauldronInteractionMixin
{
    // method_32222 is the lambda of CauldronInteraction.EMPTY.put(Items.POTION, <lambda>)
    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "method_32222", at = @At("HEAD"), cancellable = true)
    private static void handleEmptyCauldronAndPotionInteraction(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack, CallbackInfoReturnable<ItemInteractionResult> cir)
    {
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        Holder<Potion> potion = potionContents.potion().isPresent() ? potionContents.potion().get() : null;

        if (potion != null && potion != Potions.WATER)
        {
            ItemInteractionResult result = PotionCauldronBlockInteraction.fillEmptyCauldronWithPotion(blockState, level, blockPos, player, interactionHand, itemStack);
            cir.setReturnValue(result);
        }
    }
}
