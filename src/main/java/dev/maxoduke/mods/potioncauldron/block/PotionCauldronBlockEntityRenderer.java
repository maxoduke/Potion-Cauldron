package dev.maxoduke.mods.potioncauldron.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;

import java.util.OptionalInt;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class PotionCauldronBlockEntityRenderer implements BlockEntityRenderer<@NotNull PotionCauldronBlockEntity, @NotNull PotionCauldronBlockEntityRenderState>
{
    private static final float[] FLUID_HEIGHT = { 0, 0.5625f, 0.75f, 0.9375f };
    private static final SpriteId WATER_MATERIAL = new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.withDefaultNamespace("block/water_still"));
    private static final RenderType POTION_CAULDRON_BLOCK_RENDER_TYPE = RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);

    public PotionCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context ignored) { }

    @Override
    public @NotNull PotionCauldronBlockEntityRenderState createRenderState()
    {
        return new PotionCauldronBlockEntityRenderState();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void extractRenderState(@NonNull PotionCauldronBlockEntity blockEntity, @NonNull PotionCauldronBlockEntityRenderState blockEntityRenderState, float f, @NotNull Vec3 vec3, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay)
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
    public void submit(@NotNull PotionCauldronBlockEntityRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, net.minecraft.client.renderer.state.level.@NonNull CameraRenderState camera)
    {
        int liquidLevel = state.getLiquidLevel();
        Holder<Potion> potion = state.getPotion();
        int spriteColor = state.getSpriteColor();

        if (liquidLevel == 0 || potion == null || spriteColor == 0)
            return;

        int red = spriteColor >> 16 & 255;
        int green = spriteColor >> 8 & 255;
        int blue = spriteColor & 255;
        int alpha = 190;

        TextureAtlasSprite water = Minecraft.getInstance().getAtlasManager().get(WATER_MATERIAL);

        poseStack.pushPose();
        poseStack.translate(0, FLUID_HEIGHT[liquidLevel] + 0.001f, 0);

        float sizeFactor = 0.125f;
        float maxV = (water.getV1() - water.getV0()) * sizeFactor;
        float minV = (water.getV1() - water.getV0()) * (1 - sizeFactor);

        final int PACKED_LIGHT = 15728880;
        final int PACKED_OVERLAY = OverlayTexture.NO_OVERLAY;

        submitNodeCollector.submitCustomGeometry(poseStack, POTION_CAULDRON_BLOCK_RENDER_TYPE, (pose, consumer) ->
        {
            Matrix4f matrix = pose.pose();

            consumer.addVertex(matrix, sizeFactor, 0, 1 - sizeFactor)
                .setColor(red, green, blue, alpha)
                .setUv(water.getU0(), water.getV0() + maxV)
                .setLight(PACKED_LIGHT)
                .setOverlay(PACKED_OVERLAY)
                .setNormal(0, 1, 0);

            consumer.addVertex(matrix, 1 - sizeFactor, 0, 1 - sizeFactor)
                .setColor(red, green, blue, alpha)
                .setUv(water.getU1(), water.getV0() + maxV)
                .setLight(PACKED_LIGHT)
                .setOverlay(PACKED_OVERLAY)
                .setNormal(0, 1, 0);

            consumer.addVertex(matrix, 1 - sizeFactor, 0, sizeFactor)
                .setColor(red, green, blue, alpha)
                .setUv(water.getU1(), water.getV0() + minV)
                .setLight(PACKED_LIGHT)
                .setOverlay(PACKED_OVERLAY)
                .setNormal(0, 1, 0);

            consumer.addVertex(matrix, sizeFactor, 0, sizeFactor)
                .setColor(red, green, blue, alpha)
                .setUv(water.getU0(), water.getV0() + minV)
                .setLight(PACKED_LIGHT)
                .setOverlay(PACKED_OVERLAY)
                .setNormal(0, 1, 0);
        });

        poseStack.popPose();
    }
}
