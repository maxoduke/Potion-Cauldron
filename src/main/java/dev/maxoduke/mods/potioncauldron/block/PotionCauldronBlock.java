package dev.maxoduke.mods.potioncauldron.block;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.util.ParticleUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PotionCauldronBlock extends LayeredCauldronBlock implements EntityBlock
{
    public static final String NAME = "potion_cauldron";

    public PotionCauldronBlock(Properties properties, Map<Item, CauldronInteraction> map)
    {
        super(properties, null, map);
    }

    @Override
    protected boolean canReceiveStalactiteDrip(Fluid fluid) { return false; }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) { }

    @Override
    protected void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) { }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new PotionCauldronBlockEntity(pos, state);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.isClientSide || !(entity instanceof LivingEntity livingEntity) || !this.isEntityInsideContent(state, pos, entity))
            return;

        if (livingEntity.isOnFire())
        {
            livingEntity.clearFire();
            if (livingEntity.mayInteract(level, pos))
                this.handleEntityOnFireInside(state, level, pos);
        }

        ServerConfig serverConfig = PotionCauldron.CONFIG_MANAGER.serverConfig();
        if (!serverConfig.shouldApplyPotionEffectsToEntitiesInside())
            return;

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(pos);
        if (blockEntity == null)
            return;

        Potion potion = blockEntity.getPotion();

        for (var potionEffect : potion.getEffects())
        {
            MobEffect effect = potionEffect.getEffect();
            if (livingEntity.hasEffect(effect) || effect.isInstantenous())
                continue;

            int duration = serverConfig.getPotionEffectDurationInSeconds();
            if (duration != -1)
                duration *= 20;

            MobEffectInstance effectInstance = new MobEffectInstance(potionEffect.getEffect(), duration, potionEffect.getAmplifier());
            livingEntity.addEffect(effectInstance);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    @SuppressWarnings("DataFlowIssue")
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(pos);
        Potion potion = blockEntity.getPotion();

        ParticleUtils.generatePotionParticles(level, pos, PotionUtils.getColor(potion), false);
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("resource")
    public static ItemStack pickedWithPickBlock(Player player, HitResult result)
    {
        if (!(result instanceof BlockHitResult blockHitResult) || blockHitResult.getType() == HitResult.Type.MISS)
            return ItemStack.EMPTY;

        BlockPos pos = blockHitResult.getBlockPos();
        Block block = player.level().getBlockState(pos).getBlock();

        if (!(block instanceof PotionCauldronBlock))
            return ItemStack.EMPTY;

        return new ItemStack(Items.CAULDRON);
    }
}
