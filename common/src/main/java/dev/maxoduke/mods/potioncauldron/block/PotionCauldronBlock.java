package dev.maxoduke.mods.potioncauldron.block;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.util.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PotionCauldronBlock extends LayeredCauldronBlock implements EntityBlock
{
    public PotionCauldronBlock(Properties properties, Map<Item, CauldronInteraction> map)
    {
        super(properties, LayeredCauldronBlock.RAIN, map);
    }

    @Override
    public void handlePrecipitation(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Biome.@NotNull Precipitation precipitation)
    {
        if (PotionCauldron.CONFIG_MANAGER.serverConfig().shouldAllowFillingWithWaterDrips())
            super.handlePrecipitation(state, level, pos, precipitation);
    }

    @Override
    protected void receiveStalactiteDrip(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Fluid fluid)
    {
        if (PotionCauldron.CONFIG_MANAGER.serverConfig().shouldAllowFillingWithWaterDrips())
            super.receiveStalactiteDrip(state, level, pos, fluid);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
    {
        return new PotionCauldronBlockEntity(pos, state);
    }

    @Override
    public void entityInside(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Entity entity)
    {
        if (level.isClientSide || !(entity instanceof LivingEntity livingEntity) || !this.isEntityInsideContent(state, pos, entity))
            return;

        if (livingEntity instanceof ArmorStand)
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

            int duration = potionEffect.getDuration();

            boolean isPlayer = livingEntity instanceof ServerPlayer;
            boolean isPlayerAndCreative = isPlayer && ((ServerPlayer) livingEntity).isCreative();

            if (!isPlayer || !isPlayerAndCreative)
                lowerFillLevel(level.getBlockState(pos), level, pos);

            MobEffectInstance effectInstance = new MobEffectInstance(potionEffect.getEffect(), duration, potionEffect.getAmplifier());
            livingEntity.addEffect(effectInstance);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void animateTick(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull RandomSource random)
    {
        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(pos);
        Potion potion = blockEntity.getPotion();

        ParticleUtils.generatePotionParticles(level, pos, PotionUtils.getColor(potion), false);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull BlockState blockState)
    {
        return new ItemStack(Items.CAULDRON);
    }
}
