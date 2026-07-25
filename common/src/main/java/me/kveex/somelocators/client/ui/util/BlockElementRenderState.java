package me.kveex.somelocators.client.ui.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record BlockElementRenderState(
    BlockState state,
    @Nullable BlockEntityRenderState entity,
    ScreenRectangle bounds,
    ScreenRectangle scissorArea
) implements PictureInPictureRenderState {

    @Override
    public int x0() {
        return this.bounds.left();
    }

    @Override
    public int x1() {
        return this.bounds.right();
    }

    @Override
    public int y0() {
        return this.bounds.top();
    }

    @Override
    public int y1() {
        return this.bounds.bottom();
    }

    @Override
    public float scale() {
        return 1;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return this.scissorArea;
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return this.scissorArea != null ? this.scissorArea.intersection(this.bounds) : this.bounds;
    }

    public static class Renderer extends PictureInPictureRenderer<BlockElementRenderState> {

        public Renderer(MultiBufferSource.BufferSource vertexConsumers) {
            super(vertexConsumers);
        }

        @Override
        public @NonNull Class<BlockElementRenderState> getRenderStateClass() {
            return BlockElementRenderState.class;
        }

        @Override
        protected void renderToTexture(BlockElementRenderState state, PoseStack matrices) {
            var lighting = Minecraft.getInstance().gameRenderer.getLighting();
            lighting.setupFor(Lighting.Entry.ITEMS_3D);

            var width = state.bounds.width();
            var height = state.bounds.height();
            float baseSize = Math.min(width, height);
            float uniformScale = 40 * baseSize / 64f;

            matrices.translate(0, -height / 2f, 100);
            matrices.scale(uniformScale, -uniformScale, -uniformScale);

            matrices.mulPose(Axis.XP.rotationDegrees(30));
            matrices.mulPose(Axis.YP.rotationDegrees(45));

            matrices.translate(-.5, -.5, -.5);
            if (state.state.getRenderShape() != RenderShape.INVISIBLE) {
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                        state.state, matrices, bufferSource,
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY
                );
                bufferSource.endBatch();
            }

            if (state.entity != null) {
                var entityRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(state.entity);
                if (entityRenderer != null) {
                    FeatureRenderDispatcher dispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
                    entityRenderer.submit(state.entity, matrices, dispatcher.getSubmitNodeStorage(), new CameraRenderState());
                    dispatcher.renderAllFeatures();
                }
            }
        }

        @Override
        protected @NonNull String getTextureLabel() {
            return "some_locators_block";
        }
    }
}
