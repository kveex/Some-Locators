package me.kveex.somelocators.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.kveex.somelocators.block.LocatorInspectorBlock;
import me.kveex.somelocators.block.entity.LocatorInspectorBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class LocatorInspectorBlockEntityRenderer implements BlockEntityRenderer<LocatorInspectorBlockEntity, LocatorInspectorRenderState> {
    private final ItemModelResolver itemModelResolver;
    public LocatorInspectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public @NonNull LocatorInspectorRenderState createRenderState() {
        return new LocatorInspectorRenderState();
    }

    @Override
    public void extractRenderState(@NonNull LocatorInspectorBlockEntity blockEntity, @NonNull LocatorInspectorRenderState renderState, float partialTick, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        ItemStack punchCard = blockEntity.getPunchCard();
        renderState.hasPunchCard = !punchCard.isEmpty();
        renderState.facing = blockEntity.getBlockState().getValue(LocatorInspectorBlock.FACING);

        this.itemModelResolver.updateForTopItem(
                renderState.punchCardRenderState,
                punchCard,
                ItemDisplayContext.FIXED,
                null, null, 0
        );

    }

    @Override
    public void submit(LocatorInspectorRenderState state, @NonNull PoseStack matrices, @NonNull SubmitNodeCollector queue, @NonNull CameraRenderState cameraState) {
//        if (state.displayItem != null) {
//            matrices.pushPose();
//            matrices.translate(0.79D, 0.68D, 0.5D);
//            matrices.scale(0.8f, 0.8f, 0.8f);
//            matrices.mulPose(Axis.XP.rotationDegrees(90.0f));
//            matrices.mulPose(Axis.ZP.rotationDegrees(45.0f));
//            ItemEntityRenderer.renderMultipleFromCount(matrices, queue, state.lightCoords, state.displayItem, this.random);
//            matrices.popPose();
//        }
        if (!state.hasPunchCard) return;
        matrices.pushPose();
            matrices.translate(0.79D, 0.68D, 0.5D);
            matrices.scale(0.8f, 0.8f, 0.8f);
            matrices.mulPose(Axis.XP.rotationDegrees(90.0f));
            matrices.mulPose(Axis.ZP.rotationDegrees(45.0f));
            state.punchCardRenderState.submit(matrices, queue,
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0x00000000
            );
        matrices.popPose();
    }
}
