package dev.maxoduke.mods.potioncauldron.mixin;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntity;
import dev.maxoduke.mods.potioncauldron.util.PotionRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutPiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@SuppressWarnings({ "DataFlowIssue", "unused" })
@Mixin(SwampHutPiece.class)
public class SwampHutPieceMixin
{
    @Inject(method = "postProcess", at = @At("TAIL"))
    public void postProcess(
        final WorldGenLevel level,
        final StructureManager ignoredStructureManager,
        final ChunkGenerator ignoredGenerator,
        final RandomSource ignoredRandom,
        final BoundingBox chunkBB,
        final ChunkPos ignoredChunkPos,
        final BlockPos ignoredReferencePos,
        CallbackInfo ignoredCi
    )
    {
        if (!PotionCauldron.CONFIG_MANAGER.serverConfig().shouldGenerateInSwampHuts())
            return;

        String randomPotionName = PotionRandomizer.getRandomPotion();
        if (randomPotionName == null)
            return;

        Identifier potionResource = Identifier.tryParse(randomPotionName);
        if (potionResource == null)
            return;

        Optional<Holder.Reference<Potion>> randomPotion = BuiltInRegistries.POTION.get(potionResource);
        String randomPotionType = PotionRandomizer.getRandomPotionType();
        Integer randomPotionLevel = PotionRandomizer.getRandomPotionLevel();

        if (randomPotion.isEmpty())
            return;

        SwampHutPiece swampHut = (SwampHutPiece) ((Object) this);
        swampHut.placeBlock(level, PotionCauldron.BLOCK.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, randomPotionLevel), 4, 2, 6, chunkBB);

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(swampHut.getWorldPos(4, 2, 6));
        blockEntity.setPotion(randomPotion.get());
        blockEntity.setPotionType(randomPotionType);
    }
}
