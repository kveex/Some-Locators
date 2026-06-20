package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class UiPointInfo extends AbstractWidget {
    private final PointComponent point;
    public static final int blockSize = 24;
    private final BlockElement blockElement;
    private boolean blockElementHoveredFirst = false;
    public UiPointInfo(PointComponent pointComponent) {
        super(0, 0, 120, 60, Component.empty());
        this.point = pointComponent;
        this.blockElement = new BlockElement(0, 0, blockSize, point.blockState());
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        blockElement.setX(this.getX());
        blockElement.setY(this.getY());

        if (blockElement.isHovered()) {
            blockElementHoveredFirst = true;
        }

        if (this.isHovered() && blockElementHoveredFirst && TooltipDrawer.widgetNotSet()) {
            TooltipDrawer.setHoveredWidget(this);
            TooltipRenderUtil.renderTooltipBackground(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
        } else {
            TooltipDrawer.clearHoveredWidget();
            blockElementHoveredFirst = false;
        }

        blockElement.render(graphics, mouseX, mouseY, delta);

    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

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

    public static class TooltipDrawer {
        private static UiPointInfo hoveredWidget = null;
        private static boolean moreThanOnePoint = false;

        public static void setMoreThanOnePoint(boolean value) {
            moreThanOnePoint = value;
        }

        public static void setHoveredWidget(UiPointInfo widget) {
            hoveredWidget = widget;
        }

        public static void clearHoveredWidget() {
            hoveredWidget = null;
        }

        public static boolean widgetNotSet() {
            return !moreThanOnePoint || hoveredWidget == null;
        }
    }
}
