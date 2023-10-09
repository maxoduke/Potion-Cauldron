package dev.maxoduke.mods.potioncauldron.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PotionCauldronBlockEntityRenderer implements BlockEntityRenderer<PotionCauldronBlockEntity>
{
    private static final float[] FLUID_HEIGHT = { 0, 0.5625f, 0.75f, 0.9375f };
    private static final Material WATER_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("block/water_still"));

    public PotionCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context ignored)
    {

    }

    @Override
    public void render(PotionCauldronBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        int liquidLevel = entity.getBlockState().getValue(PotionCauldronBlock.LEVEL);
        if (liquidLevel == 0)
            return;

        Potion potion = entity.getPotion();

        int color = PotionUtils.getColor(potion);
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

        consumer.vertex(matrix, sizeFactor, 0, 1 - sizeFactor).color(red, green, blue, alpha).uv(water.getU0(), water.getV0() + maxV).uv2(packedLight).overlayCoords(packedOverlay).normal(1, 1, 1).endVertex();
        consumer.vertex(matrix, 1 - sizeFactor, 0, 1 - sizeFactor).color(red, green, blue, alpha).uv(water.getU1(), water.getV0() + maxV).uv2(packedLight).overlayCoords(packedOverlay).normal(1, 1, 1).endVertex();
        consumer.vertex(matrix, 1 - sizeFactor, 0, sizeFactor).color(red, green, blue, alpha).uv(water.getU1(), water.getV0() + minV).uv2(packedLight).overlayCoords(packedOverlay).normal(1, 1, 1).endVertex();
        consumer.vertex(matrix, sizeFactor, 0, sizeFactor).color(red, green, blue, alpha).uv(water.getU0(), water.getV0() + minV).uv2(packedLight).overlayCoords(packedOverlay).normal(1, 1, 1).endVertex();

        poseStack.popPose();
    }
}
