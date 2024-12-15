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
import net.minecraft.world.InteractionResult;
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
import java.util.OptionalInt;

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

    public static InteractionResult fillEmptyCauldronWithPotion(BlockState ignored, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        ResourceLocation potionTypeResource = ResourceLocation.tryParse(itemStack.getItem().toString());
        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);

        Optional<Holder<Potion>> potionHolder = potionContents.potion();
        if (potionHolder.isEmpty())
            return InteractionResult.PASS;

        Holder<Potion> potion = potionHolder.get();
        String potionType = potionTypeResource.toString();

        if (potion == Potions.WATER || potion == Potions.AWKWARD || potion == Potions.MUNDANE || potion == Potions.THICK)
            return InteractionResult.PASS;

        level.setBlockAndUpdate(blockPos, PotionCauldron.BLOCK.get().defaultBlockState());
        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(blockPos);

        blockEntity.setPotion(potion);
        blockEntity.setPotionType(potionType);

        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        OptionalInt particleColor = PotionContents.getColorOptional(potion.value().getEffects());
        if (particleColor.isPresent())
            ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.EFFECT, blockPos, particleColor.getAsInt(), true));

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
        level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        return InteractionResult.SUCCESS_SERVER;
    }

    private static InteractionResult fillPotionCauldronWithPotion(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        ResourceLocation potionTypeResource = ResourceLocation.tryParse(itemStack.getItem().toString());
        if (potionTypeResource == null)
            return InteractionResult.PASS;

        PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContents == null)
            return InteractionResult.PASS;
        
        Optional<Holder<Potion>> potionHolder = potionContents.potion();
        if (potionHolder.isEmpty())
            return InteractionResult.PASS;

        Holder<Potion> potionInHand = potionHolder.get();
        String potionTypeInHand = potionTypeResource.toString();

        if (potionInHand == Potions.WATER || potionInHand == Potions.AWKWARD || potionInHand == Potions.MUNDANE || potionInHand == Potions.THICK)
            return InteractionResult.PASS;

        if (blockState.getValue(LayeredCauldronBlock.LEVEL) == 3)
            return InteractionResult.PASS;

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
            return InteractionResult.SUCCESS;

        level.setBlockAndUpdate(blockPos, blockState.cycle(LayeredCauldronBlock.LEVEL));

        OptionalInt particleColor = PotionContents.getColorOptional(potionInCauldron.value().getEffects());
        if (particleColor.isPresent())
            ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.EFFECT, blockPos, particleColor.getAsInt(), true));

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
        level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        return InteractionResult.SUCCESS_SERVER;
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

        Holder<Potion> potion = blockEntity.getPotion();
        if (potion == null)
            return InteractionResult.PASS;

        ResourceLocation potionTypeResourceLocation = ResourceLocation.tryParse(blockEntity.getPotionType());
        if (potionTypeResourceLocation == null)
            return InteractionResult.PASS;

        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        OptionalInt particleColor = PotionContents.getColorOptional(potion.value().getEffects());
        if (particleColor.isPresent())
            ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.EFFECT, blockPos, particleColor.getAsInt(), true));

        Optional<Holder.Reference<Item>> potionTypeHolder = BuiltInRegistries.ITEM.get(potionTypeResourceLocation);
        if (potionTypeHolder.isEmpty())
            return InteractionResult.PASS;

        Item potionType = potionTypeHolder.get().value();
        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, PotionContents.createItemStack(potionType, potion)));
        LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);

        level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

        return InteractionResult.SUCCESS_SERVER;
    }

    private static InteractionResult createTippedArrowsFromPotionCauldron(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand ignored, ItemStack stack)
    {
        if (!PotionCauldron.CONFIG_MANAGER.clientOrServerConfig().shouldAllowCreatingTippedArrows())
            return InteractionResult.PASS;

        if (blockState.getValue(LayeredCauldronBlock.LEVEL) == 0)
            return InteractionResult.PASS;

        if (level.isClientSide)
            return InteractionResult.SUCCESS;

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

        OptionalInt particleColor = PotionContents.getColorOptional(potion.value().getEffects());
        if (particleColor.isPresent())
            ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.EFFECT, blockPos, particleColor.getAsInt(), true));

        level.setBlockAndUpdate(blockPos, remainingCauldronLevels == 0 ? Blocks.CAULDRON.defaultBlockState() : blockState.setValue(PotionCauldronBlock.LEVEL, remainingCauldronLevels));
        if (!player.isCreative())
            stack.shrink(tippedArrowCount);

        Inventory inventory = player.getInventory();
        if (!inventory.add(tippedArrows))
            player.drop(tippedArrows, false);

        level.playSound(null, blockPos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);

        return InteractionResult.SUCCESS_SERVER;
    }

    private static InteractionResult handlePotionMixing(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack)
    {
        if (!PotionCauldron.CONFIG_MANAGER.clientOrServerConfig().shouldEvaporatePotionWhenMixed())
            return InteractionResult.PASS;

        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        ServerNetworking.sendParticlesToClients(new ParticlePayload(ParticleTypes.POOF, blockPos));
        level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());

        ItemStack itemToGiveBack;
        if (itemStack.getItem() == Items.WATER_BUCKET || itemStack.getItem() == Items.LAVA_BUCKET)
            itemToGiveBack = new ItemStack(Items.BUCKET);
        else
            itemToGiveBack = new ItemStack(Items.GLASS_BOTTLE);

        player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, itemToGiveBack));

        level.playSound(null, blockPos, PotionCauldron.POTION_EVAPORATES_SOUND_EVENT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);

        return InteractionResult.SUCCESS_SERVER;
    }
}
