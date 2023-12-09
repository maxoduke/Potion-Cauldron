package dev.maxoduke.mods.potioncauldron.block;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.networking.ServerNetworking;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "DataFlowIssue", "DuplicatedCode" })
public class PotionCauldronBlockInteraction
{
    public static final Map<Item, CauldronInteraction> MAP = CauldronInteraction.newInteractionMap();

    public static void bootstrap()
    {
        CauldronInteraction.EMPTY.put(Items.SPLASH_POTION, PotionCauldronBlockInteraction::fillEmptyCauldronWithPotion);
        CauldronInteraction.EMPTY.put(Items.LINGERING_POTION, PotionCauldronBlockInteraction::fillEmptyCauldronWithPotion);

        MAP.put(Items.POTION, PotionCauldronBlockInteraction::fillPotionCauldronWithPotion);
        MAP.put(Items.SPLASH_POTION, PotionCauldronBlockInteraction::fillPotionCauldronWithPotion);
        MAP.put(Items.LINGERING_POTION, PotionCauldronBlockInteraction::fillPotionCauldronWithPotion);

        MAP.put(Items.WATER_BUCKET, PotionCauldronBlockInteraction::fillPotionCauldronWithWaterOrLavaBucket);
        MAP.put(Items.LAVA_BUCKET, PotionCauldronBlockInteraction::fillPotionCauldronWithWaterOrLavaBucket);

        MAP.put(Items.GLASS_BOTTLE, PotionCauldronBlockInteraction::fillBottleFromPotionCauldron);
        MAP.put(Items.ARROW, PotionCauldronBlockInteraction::createTippedArrowsFromPotionCauldron);
    }

    public static InteractionResult fillEmptyCauldronWithPotion(BlockState ignored, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        ResourceLocation potionTypeResource = ResourceLocation.tryParse(itemStack.getItem().toString());
        if (potionTypeResource == null)
            return InteractionResult.PASS;

        Potion potion = PotionUtils.getPotion(itemStack);
        String potionType = potionTypeResource.toString();

        if (potion == Potions.EMPTY || potion == Potions.WATER || potion == Potions.AWKWARD || potion == Potions.MUNDANE || potion == Potions.THICK)
            return InteractionResult.PASS;

        level.setBlockAndUpdate(blockPos, PotionCauldron.BLOCK.defaultBlockState());

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(blockPos);
        blockEntity.setPotion(potion);
        blockEntity.setPotionType(potionType);

        if (level.isClientSide)
            return InteractionResult.sidedSuccess(true);

        ServerNetworking.sendParticlesToClients(new ParticlePacket(ParticleTypes.EFFECT, blockPos, PotionUtils.getColor(potion), true));

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
        level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        return InteractionResult.sidedSuccess(false);
    }

    private static InteractionResult fillPotionCauldronWithPotion(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        ResourceLocation potionTypeResource = ResourceLocation.tryParse(itemStack.getItem().toString());
        if (potionTypeResource == null)
            return InteractionResult.PASS;

        Potion potionInHand = PotionUtils.getPotion(itemStack);
        String potionTypeInHand = potionTypeResource.toString();

        if (potionInHand == Potions.EMPTY || potionInHand == Potions.WATER || potionInHand == Potions.AWKWARD || potionInHand == Potions.MUNDANE || potionInHand == Potions.THICK)
            return InteractionResult.PASS;

        if (blockState.getValue(LayeredCauldronBlock.LEVEL) == 3)
            return InteractionResult.PASS;

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(blockPos);
        Potion potionInCauldron = blockEntity.getPotion();
        String potionTypeInCauldron = blockEntity.getPotionType();

        if (potionInCauldron != potionInHand)
            return handlePotionMixing(level, blockPos, player, interactionHand, itemStack);

        if (!potionTypeInCauldron.equals(potionTypeInHand))
        {
            if (!PotionCauldron.CONFIG_MANAGER.clientOrServerConfig().shouldAllowMergingPotions())
                return handlePotionMixing(level, blockPos, player, interactionHand, itemStack);

            blockEntity.setPotionType(potionTypeInHand);
        }

        if (level.isClientSide)
            return InteractionResult.sidedSuccess(true);

        level.setBlockAndUpdate(blockPos, blockState.cycle(LayeredCauldronBlock.LEVEL));
        ServerNetworking.sendParticlesToClients(new ParticlePacket(ParticleTypes.EFFECT, blockPos, PotionUtils.getColor(potionInCauldron), true));

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
        level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        return InteractionResult.sidedSuccess(false);
    }

    private static InteractionResult fillPotionCauldronWithWaterOrLavaBucket(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        return handlePotionMixing(level, blockPos, player, interactionHand, itemStack);
    }

    private static InteractionResult fillBottleFromPotionCauldron(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        if (blockState.getValue(LayeredCauldronBlock.LEVEL) == 0)
            return InteractionResult.PASS;

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(blockPos);
        if (blockEntity == null)
            return InteractionResult.PASS;

        Potion potion = blockEntity.getPotion();
        if (potion == Potions.EMPTY)
            return InteractionResult.PASS;

        ResourceLocation potionTypeResourceLocation = ResourceLocation.tryParse(blockEntity.getPotionType());
        if (potionTypeResourceLocation == null)
            return InteractionResult.PASS;

        if (level.isClientSide)
            return InteractionResult.sidedSuccess(true);

        ServerNetworking.sendParticlesToClients(new ParticlePacket(ParticleTypes.EFFECT, blockPos, PotionUtils.getColor(potion), true));

        Item potionType = BuiltInRegistries.ITEM.get(potionTypeResourceLocation);

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, PotionUtils.setPotion(new ItemStack(potionType), potion)));
        LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);

        level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

        return InteractionResult.sidedSuccess(false);
    }

    private static InteractionResult createTippedArrowsFromPotionCauldron(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand ignored, ItemStack stack)
    {
        if (!PotionCauldron.CONFIG_MANAGER.clientOrServerConfig().shouldAllowCreatingTippedArrows())
            return InteractionResult.PASS;

        if (blockState.getValue(LayeredCauldronBlock.LEVEL) == 0)
            return InteractionResult.PASS;

        if (level.isClientSide)
            return InteractionResult.sidedSuccess(true);

        HashMap<Integer, Integer> cauldronLevelToArrows = PotionCauldron.CONFIG_MANAGER.serverConfig().maxTippedArrowsPerLevel();

        int currentCauldronLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
        int maxTippedArrowCount = cauldronLevelToArrows.get(currentCauldronLevel);
        int tippedArrowCount = Math.min(stack.getCount(), maxTippedArrowCount);

        int usedCauldronLevels = -1;
        for (var item : cauldronLevelToArrows.entrySet())
        {
            if (tippedArrowCount <= item.getValue())
            {
                usedCauldronLevels = item.getKey();
                break;
            }
        }
        int remainingCauldronLevels = currentCauldronLevel - usedCauldronLevels;

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(blockPos);
        Potion potion = blockEntity.getPotion();

        ItemStack tippedArrows = new ItemStack(Items.TIPPED_ARROW);
        tippedArrows.setCount(tippedArrowCount);
        PotionUtils.setPotion(tippedArrows, potion);

        ServerNetworking.sendParticlesToClients(new ParticlePacket(ParticleTypes.EFFECT, blockPos, PotionUtils.getColor(potion), true));
        level.setBlockAndUpdate(blockPos, remainingCauldronLevels == 0 ? Blocks.CAULDRON.defaultBlockState() : blockState.setValue(PotionCauldronBlock.LEVEL, remainingCauldronLevels));

        if (!player.isCreative())
            stack.shrink(tippedArrowCount);

        Inventory inventory = player.getInventory();
        if (!inventory.add(tippedArrows))
            player.drop(tippedArrows, false);

        level.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

        return InteractionResult.sidedSuccess(false);
    }

    private static InteractionResult handlePotionMixing(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        if (!PotionCauldron.CONFIG_MANAGER.clientOrServerConfig().shouldEvaporatePotionWhenMixed())
            return InteractionResult.PASS;

        if (level.isClientSide)
            return InteractionResult.sidedSuccess(true);

        ServerNetworking.sendParticlesToClients(new ParticlePacket(ParticleTypes.POOF, blockPos));
        level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());

        ItemStack itemToGiveBack;
        if (itemStack.getItem() == Items.WATER_BUCKET || itemStack.getItem() == Items.LAVA_BUCKET)
            itemToGiveBack = new ItemStack(Items.BUCKET);
        else
            itemToGiveBack = new ItemStack(Items.GLASS_BOTTLE);

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, itemToGiveBack));

        level.playSound(null, blockPos, PotionCauldron.POTION_EVAPORATES_SOUND_EVENT, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        return InteractionResult.sidedSuccess(false);
    }
}
