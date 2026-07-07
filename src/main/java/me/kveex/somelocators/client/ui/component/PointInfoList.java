package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class PointInfoList extends AbstractWidget {
    private final List<PointComponent> points;
    private double scrollOffset;
    private static final int PADDING = 8;
    private static final int TOOLTIP_PADDING = 9;
    private static final int POINT_INFO_ELEMENT_HEIGHT = 32;
    public PointInfoList(int x, int y, int width, int height, List<PointComponent> points) {
        super(x, y, width, height, Component.empty());
        this.points = points;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(this.getX() - TOOLTIP_PADDING, this.getY() - TOOLTIP_PADDING, this.getX() + this.getWidth() + TOOLTIP_PADDING, this.getY() + this.getHeight());

        double yOffset = this.getY() - scrollOffset;

        for (PointComponent pointComponent : this.points) {
            ExtendedPointInfoElement element = new ExtendedPointInfoElement(this.getX(), (int) yOffset, this.getWidth(), POINT_INFO_ELEMENT_HEIGHT, pointComponent);
            if (yOffset + element.getHeight() > this.getY() && yOffset < this.getY() + this.getHeight()) {
                element.render(graphics, mouseX, mouseY, partialTick);
            }
            yOffset += element.getHeight() + PADDING;
        }

        graphics.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (points.size() <= 3) return false;
        scrollOffset += -scrollY * 10;

        int emptyPointOffset = POINT_INFO_ELEMENT_HEIGHT + PADDING;
        int maxScroll = Math.max(0, points.size() * POINT_INFO_ELEMENT_HEIGHT - emptyPointOffset);
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);
        SomeLocators.LOGGER.info("Scroll offset: {}", scrollOffset);
        return true;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }
}
