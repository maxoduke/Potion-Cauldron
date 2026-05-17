package dev.maxoduke.mods.potioncauldron.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.OptionalInt;

public class PotionCauldronBlock extends AbstractCauldronBlock implements EntityBlock
{
    public static final MapCodec<PotionCauldronBlock> CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(
                Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter(b -> b.precipitation),
                PotionCauldronBlockInteractions.CODEC.fieldOf("interactions").forGetter(b -> b.interactionMap),
                propertiesCodec()
            )
            .apply(i, PotionCauldronBlock::new)
    );

    public static final int MIN_FILL_LEVEL = 1;
    public static final int MAX_FILL_LEVEL = 3;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;

    public static void lowerFillLevel(final BlockState state, final Level level, final BlockPos pos)
    {
        int newLevel = state.getValue(LEVEL) - 1;
        BlockState newState = newLevel < MIN_FILL_LEVEL ? Blocks.CAULDRON.defaultBlockState() : state.setValue(LEVEL, newLevel);
        level.setBlockAndUpdate(pos, newState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
    }

    private final Biome.Precipitation precipitation;
    private final CauldronInteraction.Dispatcher interactionMap;

    public PotionCauldronBlock(Biome.Precipitation precipitation, CauldronInteraction.Dispatcher interactionMap, Properties properties)
    {
        super(properties, interactionMap);
        this.precipitation = precipitation;
        this.interactionMap = interactionMap;

        registerDefaultState(defaultBlockState().setValue(LEVEL, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LEVEL);
    }

    @Override
    public boolean isFull(final BlockState state)
    {
        return state.getValue(LEVEL) == MAX_FILL_LEVEL;
    }

    @Override
    protected boolean canReceiveStalactiteDrip(final @NonNull Fluid fluid)
    {
        return this.precipitation == Biome.Precipitation.RAIN;
    }

    @Override
    public void handlePrecipitation(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Biome.@NotNull Precipitation precipitation)
    {
        if (!PotionCauldron.CONFIG_MANAGER.serverConfig().shouldAllowFillingWithWaterDrips())
            return;

        if (state.getValue(LEVEL) == MAX_FILL_LEVEL || precipitation != this.precipitation)
            return;

        BlockState newState = state.cycle(LEVEL);
        level.setBlockAndUpdate(pos, newState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
    }

    @Override
    protected void receiveStalactiteDrip(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Fluid fluid)
    {
        if (!PotionCauldron.CONFIG_MANAGER.serverConfig().shouldAllowFillingWithWaterDrips())
            return;

        if (isFull(state))
            return;

        BlockState newState = state.cycle(LEVEL);
        level.setBlockAndUpdate(pos, newState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
    {
        return new PotionCauldronBlockEntity(pos, state);
    }

    @Override
    public void entityInside(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Entity entity, @NotNull InsideBlockEffectApplier insideBlockEffectApplier, boolean bl)
    {
        if (level.isClientSide() || !(entity instanceof LivingEntity livingEntity))
            return;

        if (livingEntity instanceof ArmorStand)
            return;

        if (livingEntity.isOnFire())
        {
            livingEntity.clearFire();
            if (livingEntity.mayInteract((ServerLevel) level, blockPos))
                lowerFillLevel(blockState, level, blockPos);
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

    @Override
    public @NonNull MapCodec<PotionCauldronBlock> codec()
    {
        return CODEC;
    }
}