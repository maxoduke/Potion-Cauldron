package dev.maxoduke.mods.potioncauldron.mixin;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntity;
import dev.maxoduke.mods.potioncauldron.util.PotionRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

@Mixin(SwampHutPiece.class)
@SuppressWarnings("DataFlowIssue")
public class SwampHutPieceMixin
{
    @Inject(method = "postProcess", at = @At("TAIL"))
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos, CallbackInfo callbackInfo)
    {
        if (!PotionCauldron.CONFIG_MANAGER.serverConfig().shouldGenerateInSwampHuts())
            return;

        String randomPotionName = PotionRandomizer.getRandomPotion();
        if (randomPotionName == null)
            return;

        ResourceLocation potionResource = ResourceLocation.tryParse(randomPotionName);
        if (potionResource == null)
            return;

        Potion randomPotion = BuiltInRegistries.POTION.get(potionResource);
        String randomPotionType = PotionRandomizer.getRandomPotionType();
        Integer randomPotionLevel = PotionRandomizer.getRandomPotionLevel();

        SwampHutPiece swampHut = (SwampHutPiece) ((Object) this);
        swampHut.placeBlock(level, PotionCauldron.BLOCK.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, randomPotionLevel), 4, 2, 6, box);

        PotionCauldronBlockEntity blockEntity = (PotionCauldronBlockEntity) level.getBlockEntity(swampHut.getWorldPos(4, 2, 6));
        blockEntity.setPotion(randomPotion);
        blockEntity.setPotionType(randomPotionType);
    }
}
