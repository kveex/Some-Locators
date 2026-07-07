package me.kveex.somelocators.client.ui.element;

import me.kveex.somelocators.client.ui.util.BlockElementRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.Collections;

public class BlockElement extends AbstractWidget {
    public static final Minecraft client = Minecraft.getInstance();
    private final BlockState blockState;

    private final boolean showBlockNameOnHover;

    public BlockElement(int x, int y, int size, BlockState blockState) {
        super(x, y, size, size, Component.empty());
        this.blockState = blockState;
        this.active = false;
        this.showBlockNameOnHover = false;
    }

    public BlockElement(int x, int y, int size, BlockState blockState, boolean showBlockNameOnHover) {
        super(x, y, size, size, Component.empty());
        this.blockState = blockState;
        this.showBlockNameOnHover = showBlockNameOnHover;
        this.active = false;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        //Thanks owo lib for this code :3
        BlockEntityRenderState renderedEntity = null;
        if (client.player != null) {
            if (this.blockState.getBlock() instanceof EntityBlock provider) {
                BlockEntity blockEntity = provider.newBlockEntity(client.player.blockPosition(), blockState);
                if (blockEntity != null) {
                    var renderer = client.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
                    if (renderer != null) {
                        renderedEntity = renderer.createRenderState();

                        renderer.extractRenderState(
                                blockEntity, renderedEntity, delta, Vec3.ZERO, null
                        );
                    }
                }
            }
        }

        graphics.guiRenderState.submitPicturesInPictureState(
                new BlockElementRenderState(
                        this.blockState.getBlock().defaultBlockState().rotate(Rotation.CLOCKWISE_180),
                        renderedEntity,
                        new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()),
                        graphics.scissorStack.peek()
                )
        );

        if (this.isHovered() && this.showBlockNameOnHover) {
            Component blockName = Component.translatable(this.blockState.getBlock().getDescriptionId());
            graphics.setComponentTooltipForNextFrame(client.font, Collections.singletonList(blockName), mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        return false;
    }
}
