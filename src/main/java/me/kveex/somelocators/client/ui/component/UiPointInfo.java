package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class UiPointInfo extends AbstractWidget {
    private final PointComponent point;
    private static final int blockSize = 24;
    private static final int MARGIN = 8;
    private final BlockElement blockElement;
    private boolean blockElementHoveredFirst = false;
    private final boolean isTracked;
    private static final Identifier TARGETED_FRAME_SPRITE = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "textures/gui/targeted_point_frame.png");
    private static final Identifier TOOLTIP_SPRITE = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "locator_point");
    public UiPointInfo(PointComponent pointComponent, boolean isTracked) {
        super(0, 0, 120, 60, Component.empty());
        this.point = pointComponent;
        this.isTracked = isTracked;
        this.blockElement = new BlockElement(0, 0, blockSize, point.blockState());
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        blockElement.setX(this.getX());
        blockElement.setY(this.getY());

        if (blockElement.isHovered()) {
            blockElementHoveredFirst = true;
        }

        if (this.isHovered() && TooltipDrawer.canClaim(this)) {
            TooltipDrawer.setHoveredWidget(this);
            showAllInfo(graphics);
        } else {
            if (TooltipDrawer.isHoveredWidget(this)) {
                TooltipDrawer.clearHoveredWidget();
            }
            blockElementHoveredFirst = false;
        }

        blockElement.render(graphics, mouseX, mouseY, delta);
        if (this.isTracked) renderTrackedPointFrame(graphics);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean isHovered() {
        return super.isHovered() && blockElementHoveredFirst;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        return false;
    }

    public UiPointInfo pos(int x, int y) {
        this.setX(x);
        this.setY(y);
        return this;
    }

    private void showAllInfo(@NonNull GuiGraphics graphics) {
        LabelElement pointName = new LabelElement(this.getX() + blockSize + 4, this.getY() + 4, Component.literal(point.name()));
        int fitLabelWidth = blockSize + pointName.getWidth() + MARGIN;
        if (fitLabelWidth > this.getWidth()) this.setWidth(fitLabelWidth);

        renderTooltipBackground(graphics);
        pointName.render(graphics);
    }

    private void renderTooltipBackground(@NonNull GuiGraphics graphics) {
        TooltipRenderUtil.renderTooltipBackground(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), TOOLTIP_SPRITE);
    }

    private void renderTrackedPointFrame(@NonNull GuiGraphics graphics) {
        int u = 0, v = 0;
        int x = blockElement.getX() - MARGIN + 6, y = blockElement.getY() - MARGIN + 6;
        int width = blockElement.getWidth() + MARGIN * 2 - 12, height = blockElement.getHeight() + MARGIN * 2 - 12;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TARGETED_FRAME_SPRITE, x, y, u, v, width, height, width, height);
    }

    public static class TooltipDrawer {
        private static UiPointInfo hoveredWidget = null;
        private static boolean moreThanOnePoint = false;

        public static void setMoreThanOnePoint(boolean value) {
            moreThanOnePoint = value;
        }

        public static void setHoveredWidget(UiPointInfo widget) {
            hoveredWidget = widget;
        }

        public static boolean isHoveredWidget(UiPointInfo widget) {
            return hoveredWidget == widget;
        }

        public static void clearHoveredWidget() {
            hoveredWidget = null;
        }

        public static boolean canClaim(UiPointInfo widget) {
            return !moreThanOnePoint || hoveredWidget == null || hoveredWidget == widget;
        }
    }
}
