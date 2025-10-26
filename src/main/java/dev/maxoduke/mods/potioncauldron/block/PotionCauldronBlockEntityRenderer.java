package dev.maxoduke.mods.potioncauldron.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.OptionalInt;

public class PotionCauldronBlockEntityRenderer implements BlockEntityRenderer<PotionCauldronBlockEntity, PotionCauldronBlockEntityRenderState>
{
    @SuppressWarnings("deprecation")
    private static final Material WATER_MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_still"));
    private static final float[] FLUID_HEIGHT = { 0, 0.5625f, 0.75f, 0.9375f };

    public PotionCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context ignored) { }

    @Override
    public @NotNull PotionCauldronBlockEntityRenderState createRenderState()
    {
        return new PotionCauldronBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(@NotNull PotionCauldronBlockEntity blockEntity, @NotNull PotionCauldronBlockEntityRenderState blockEntityRenderState, float f, @NotNull Vec3 vec3, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay)
    {
        BlockEntityRenderer.super.extractRenderState(blockEntity, blockEntityRenderState, f, vec3, crumblingOverlay);

        int liquidLevel = blockEntity.getBlockState().getValue(PotionCauldronBlock.LEVEL);
        if (liquidLevel == 0)
            return;

        Holder<Potion> potion = blockEntity.getPotion();
        if (potion == null)
            return;

        OptionalInt spriteColor = PotionContents.getColorOptional(potion.value().getEffects());
        if (spriteColor.isEmpty())
            return;

        blockEntityRenderState.setLiquidLevel(liquidLevel);
        blockEntityRenderState.setPotion(potion);
        blockEntityRenderState.setSpriteColor(spriteColor.getAsInt());
    }

    @Override
    public void submit(PotionCauldronBlockEntityRenderState blockEntityRenderState, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, @NotNull CameraRenderState cameraRenderState)
    {
        int liquidLevel = blockEntityRenderState.getLiquidLevel();
        Holder<Potion> potion = blockEntityRenderState.getPotion();
        int spriteColor = blockEntityRenderState.getSpriteColor();

        if (liquidLevel == 0 || potion == null || spriteColor == 0)
            return;

        int red = spriteColor >> 16 & 255;
        int green = spriteColor >> 8 & 255;
        int blue = spriteColor & 255;
        int alpha = 190;

        TextureAtlasSprite water = Minecraft.getInstance().getAtlasManager().get(WATER_MATERIAL);

        poseStack.pushPose();
        poseStack.translate(0, FLUID_HEIGHT[liquidLevel], 0);

        Matrix4f matrix = poseStack.last().pose();

        float sizeFactor = 0.125f;
        float maxV = (water.getV1() - water.getV0()) * sizeFactor;
        float minV = (water.getV1() - water.getV0()) * (1 - sizeFactor);

        final int PACKED_LIGHT = 15728880;
        final int PACKED_OVERLAY = OverlayTexture.NO_OVERLAY;

        submitNodeCollector.submitCustomGeometry(poseStack, RenderType.translucentMovingBlock(), (pose, consumer) ->
        {
            consumer.addVertex(matrix, sizeFactor, 0, 1 - sizeFactor)
                .setColor(red, green, blue, alpha)
                .setUv(water.getU0(), water.getV0() + maxV)
                .setLight(PACKED_LIGHT)
                .setOverlay(PACKED_OVERLAY)
                .setNormal(1, 1, 1);

            consumer.addVertex(matrix, 1 - sizeFactor, 0, 1 - sizeFactor).setColor(red, green, blue, alpha).setUv(water.getU1(), water.getV0() + maxV).setLight(PACKED_LIGHT).setOverlay(PACKED_OVERLAY).setNormal(1, 1, 1);
            consumer.addVertex(matrix, 1 - sizeFactor, 0, sizeFactor).setColor(red, green, blue, alpha).setUv(water.getU1(), water.getV0() + minV).setLight(PACKED_LIGHT).setOverlay(PACKED_OVERLAY).setNormal(1, 1, 1);
            consumer.addVertex(matrix, sizeFactor, 0, sizeFactor).setColor(red, green, blue, alpha).setUv(water.getU0(), water.getV0() + minV).setLight(PACKED_LIGHT).setOverlay(PACKED_OVERLAY).setNormal(1, 1, 1);
        });


        poseStack.popPose();
    }
}
