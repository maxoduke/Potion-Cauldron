package dev.maxoduke.mods.potioncauldron.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class PotionCauldronBlockEntityRenderer implements BlockEntityRenderer<PotionCauldronBlockEntity>
{
    private static final float[] FLUID_HEIGHT = { 0, 0.5625f, 0.75f, 0.9375f };
    private static final Material WATER_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.withDefaultNamespace("block/water_still"));

    public PotionCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context ignored)
    {

    }

    @Override
    public void render(PotionCauldronBlockEntity entity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        int liquidLevel = entity.getBlockState().getValue(PotionCauldronBlock.LEVEL);
        if (liquidLevel == 0)
            return;

        Holder<Potion> potion = entity.getPotion();
        if (potion == null)
            return;

        int color = PotionContents.getColor(potion.value().getEffects());
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int alpha = 190;

        TextureAtlasSprite water = WATER_MATERIAL.sprite();

        poseStack.pushPose();
        poseStack.translate(0, FLUID_HEIGHT[liquidLevel], 0);

        VertexConsumer consumer = buffer.getBuffer(RenderType.translucentMovingBlock());
        Matrix4f matrix = poseStack.last().pose();

        float sizeFactor = 0.125f;
        float maxV = (water.getV1() - water.getV0()) * sizeFactor;
        float minV = (water.getV1() - water.getV0()) * (1 - sizeFactor);

        consumer.addVertex(matrix, sizeFactor, 0, 1 - sizeFactor).setColor(red, green, blue, alpha).setUv(water.getU0(), water.getV0() + maxV).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 1, 1);
        consumer.addVertex(matrix, 1 - sizeFactor, 0, 1 - sizeFactor).setColor(red, green, blue, alpha).setUv(water.getU1(), water.getV0() + maxV).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 1, 1);
        consumer.addVertex(matrix, 1 - sizeFactor, 0, sizeFactor).setColor(red, green, blue, alpha).setUv(water.getU1(), water.getV0() + minV).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 1, 1);
        consumer.addVertex(matrix, sizeFactor, 0, sizeFactor).setColor(red, green, blue, alpha).setUv(water.getU0(), water.getV0() + minV).setLight(packedLight).setOverlay(packedOverlay).setNormal(1, 1, 1);

        poseStack.popPose();
    }
}
