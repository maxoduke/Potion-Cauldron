package dev.maxoduke.mods.potioncauldron.block;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.networking.ServerNetworking;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ParticlePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({ "DuplicatedCode", "DataFlowIssue" })
public class PotionCauldronBlockInteraction
{
    public static final CauldronInteraction.InteractionMap INTERACTION_MAP = CauldronInteraction.newInteractionMap("PotionCauldronInteractionMap");
    public static final Map<Item, CauldronInteraction> MAP = INTERACTION_MAP.map();

    public static void bootstrap()
    {
        CauldronInteraction.EMPTY.map().put(Items.SPLASH_POTION, PotionCauldronBlockInteraction::fillEmptyCauldronWithPotion);
        CauldronInteraction.EMPTY.map().put(Items.LINGERING_POTION, PotionCauldronBlockInteraction::fillEmptyCauldronWithPotion);

        MAP.put(Items.POTION, PotionCauldronBlockInteraction::fillPotionCauldronWithPotion);
        MAP.put(Items.SPLASH_POTION, PotionCauldronBlockInteraction::fillPotionCauldronWithPotion);
        MAP.put(Items.LINGERING_POTION, PotionCauldronBlockInteraction::fillPotionCauldronWithPotion);

        MAP.put(Items.WATER_BUCKET, PotionCauldronBlockInteraction::fillPotionCauldronWithWaterOrLavaBucket);
        MAP.put(Items.LAVA_BUCKET, PotionCauldronBlockInteraction::fillPotionCauldronWithWaterOrLavaBucket);

        MAP.put(Items.GLASS_BOTTLE, PotionCauldronBlockInteraction::fillBottleFromPotionCauldron);
        MAP.put(Items.ARROW, PotionCauldronBlockInteraction::createTippedArrowsFromPotionCauldron);
    }

    public static ItemInteractionResult fillEmptyCauldronWithPotion(BlockState ignored, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        ResourceLocation potionTypeResource = ResourceLocation.tryParse(itemStack.getItem().toString());
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);

        Optional<Holder<Potion>> potionHolder = potionContents.potion();
        if (potionHolder.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        Holder<Potion> potion = potionHolder.get();
        String potionType = potionTypeResource.toString();

        if (potion == Potions.WATER || potion == Potions.AWKWARD || potion == Potions.MUNDANE || potion == Potions.THICK)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        level.setBlockAndUpdate(blockPos, PotionCauldron.BLOCK.defaultBlockState());
        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(blockPos);

        blockEntity.setPotion(potion);
        blockEntity.setPotionType(potionType);

        if (level.isClientSide)
            return ItemInteractionResult.sidedSuccess(true);

        ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.EFFECT, blockPos, PotionContents.getColor(potion.value().getEffects()), true));

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
        level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        return ItemInteractionResult.sidedSuccess(false);
    }

    private static ItemInteractionResult fillPotionCauldronWithPotion(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        ResourceLocation potionTypeResource = ResourceLocation.tryParse(itemStack.getItem().toString());
        if (potionTypeResource == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContents == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        
        Optional<Holder<Potion>> potionHolder = potionContents.potion();
        if (potionHolder.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        Holder<Potion> potionInHand = potionHolder.get();
        String potionTypeInHand = potionTypeResource.toString();

        if (potionInHand == Potions.WATER || potionInHand == Potions.AWKWARD || potionInHand == Potions.MUNDANE || potionInHand == Potions.THICK)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (blockState.getValue(LayeredCauldronBlock.LEVEL) == 3)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(blockPos);
        Holder<Potion> potionInCauldron = blockEntity.getPotion();
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
            return ItemInteractionResult.sidedSuccess(true);

        level.setBlockAndUpdate(blockPos, blockState.cycle(LayeredCauldronBlock.LEVEL));
        ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.EFFECT, blockPos, PotionContents.getColor(potionInCauldron.value().getEffects()), true));

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
        level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        return ItemInteractionResult.sidedSuccess(false);
    }

    private static ItemInteractionResult fillPotionCauldronWithWaterOrLavaBucket(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        return handlePotionMixing(level, blockPos, player, interactionHand, itemStack);
    }

    private static ItemInteractionResult fillBottleFromPotionCauldron(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        if (blockState.getValue(LayeredCauldronBlock.LEVEL) == 0)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(blockPos);
        if (blockEntity == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        Holder<Potion> potion = blockEntity.getPotion();
        if (potion == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        ResourceLocation potionTypeResourceLocation = ResourceLocation.tryParse(blockEntity.getPotionType());
        if (potionTypeResourceLocation == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (level.isClientSide)
            return ItemInteractionResult.sidedSuccess(true);

        ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.EFFECT, blockPos, PotionContents.getColor(potion.value().getEffects()), true));

        Item potionType = BuiltInRegistries.ITEM.get(potionTypeResourceLocation);

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, PotionContents.createItemStack(potionType, potion)));
        LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);

        level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

        return ItemInteractionResult.sidedSuccess(false);
    }

    private static ItemInteractionResult createTippedArrowsFromPotionCauldron(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand ignored, ItemStack stack)
    {
        if (!PotionCauldron.CONFIG_MANAGER.clientOrServerConfig().shouldAllowCreatingTippedArrows())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (blockState.getValue(LayeredCauldronBlock.LEVEL) == 0)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (level.isClientSide)
            return ItemInteractionResult.sidedSuccess(true);

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
        Holder<Potion> potion = blockEntity.getPotion();

        ItemStack tippedArrows = new ItemStack(Items.TIPPED_ARROW);
        tippedArrows.setCount(tippedArrowCount);
        tippedArrows.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));

        ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.EFFECT, blockPos, PotionContents.getColor(potion.value().getEffects()), true));
        level.setBlockAndUpdate(blockPos, remainingCauldronLevels == 0 ? Blocks.CAULDRON.defaultBlockState() : blockState.setValue(PotionCauldronBlock.LEVEL, remainingCauldronLevels));

        if (!player.isCreative())
            stack.shrink(tippedArrowCount);

        Inventory inventory = player.getInventory();
        if (!inventory.add(tippedArrows))
            player.drop(tippedArrows, false);

        level.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

        return ItemInteractionResult.sidedSuccess(false);
    }

    private static ItemInteractionResult handlePotionMixing(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        if (!PotionCauldron.CONFIG_MANAGER.clientOrServerConfig().shouldEvaporatePotionWhenMixed())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (level.isClientSide)
            return ItemInteractionResult.sidedSuccess(true);

        ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.POOF, blockPos));
        level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());

        ItemStack itemToGiveBack;
        if (itemStack.getItem() == Items.WATER_BUCKET || itemStack.getItem() == Items.LAVA_BUCKET)
            itemToGiveBack = new ItemStack(Items.BUCKET);
        else
            itemToGiveBack = new ItemStack(Items.GLASS_BOTTLE);

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, itemToGiveBack));

        level.playSound(null, blockPos, PotionCauldron.POTION_EVAPORATES_SOUND_EVENT, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        return ItemInteractionResult.sidedSuccess(false);
    }
}
