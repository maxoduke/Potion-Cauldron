package dev.maxoduke.mods.potioncauldron.block;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.util.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public class PotionCauldronBlock extends LayeredCauldronBlock implements EntityBlock
{
    public PotionCauldronBlock(CauldronInteraction.InteractionMap interactionMap, Properties properties)
    {
        super(Biome.Precipitation.RAIN, interactionMap, properties);
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
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, boolean bl)
    {
        if (level.isClientSide() || !(entity instanceof LivingEntity livingEntity))
            return;

        if (livingEntity instanceof ArmorStand)
            return;

        if (livingEntity.isOnFire())
        {
            livingEntity.clearFire();
            if (livingEntity.mayInteract((ServerLevel) level, blockPos))
                this.handleEntityOnFireInside(blockState, level, blockPos);
        }

        ServerConfig serverConfig = PotionCauldron.CONFIG_MANAGER.serverConfig();
        if (!serverConfig.shouldApplyPotionEffectsToEntitiesInside())
            return;

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(blockPos);
        if (blockEntity == null)
            return;

        Holder<Potion> potionHolder = blockEntity.getPotion();
        if (potionHolder == null)
            return;

        Potion potion = potionHolder.value();
        if (potion.hasInstantEffects())
            return;

        for (var potionEffect : potion.getEffects())
        {
            Holder<MobEffect> effect = potionEffect.getEffect();
            if (livingEntity.hasEffect(effect))
                continue;

            int duration = potionEffect.getDuration();

            boolean isPlayer = livingEntity instanceof ServerPlayer;
            boolean isPlayerAndCreative = isPlayer && ((ServerPlayer) livingEntity).isCreative();

            if (!isPlayer || !isPlayerAndCreative)
                lowerFillLevel(level.getBlockState(blockPos), level, blockPos);

            MobEffectInstance effectInstance = new MobEffectInstance(potionEffect.getEffect(), duration, potionEffect.getAmplifier());
            livingEntity.addEffect(effectInstance);
        }
    }

    @Override
    public void animateTick(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull RandomSource random)
    {
        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(pos);
        if (blockEntity == null)
            return;

        Holder<Potion> potion = blockEntity.getPotion();
        if (potion == null)
            return;

        OptionalInt particleColor = PotionContents.getColorOptional(potion.value().getEffects());
        if (particleColor.isEmpty())
            return;

        ParticleUtils.generatePotionParticles(level, pos, particleColor.getAsInt(), false);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull LevelReader reader, @NotNull BlockPos blockPos, @NotNull BlockState blockState, boolean bl)
    {
        return new ItemStack(Items.CAULDRON);
    }
}
