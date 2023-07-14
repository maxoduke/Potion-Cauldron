package dev.maxoduke.mods.potioncauldron.mixin;

import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
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
    @Inject(method = "method_32222", at = @At("HEAD"), cancellable = true)
    private static void handleEmptyCauldronAndPotionInteraction(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack, CallbackInfoReturnable<InteractionResult> cir)
    {
        if (PotionUtils.getPotion(itemStack) != Potions.WATER)
        {
            InteractionResult result = PotionCauldronBlockInteraction.fillEmptyCauldronWithPotion(blockState, level, blockPos, player, interactionHand, itemStack);
            cir.setReturnValue(result);
        }
    }
}
