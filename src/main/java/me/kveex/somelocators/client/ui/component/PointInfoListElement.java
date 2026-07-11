package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.component.PointComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class PointInfoListElement extends AbstractWidget {
    private final List<PointComponent> points;
    private double scrollOffset;
    private static final int POINT_INFO_ELEMENT_HEIGHT = 40;
    private final List<ExtendedPointInfoElement> elements;

    public PointInfoListElement(int x, int y, int width, int height, List<PointComponent> points) {
        super(x, y, width, height, Component.empty());
        this.points = points;
        this.elements = points.stream()
                .map(point -> new ExtendedPointInfoElement(this.getX(), 0, this.getWidth(), POINT_INFO_ELEMENT_HEIGHT, point))
                .toList();
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());

        double yOffset = this.getY() - scrollOffset;

        for (ExtendedPointInfoElement element : this.elements) {
            if (yOffset + element.getHeight() > this.getY() && yOffset < this.getY() + this.getHeight()) {
                element.setY((int) yOffset);
                element.render(graphics, mouseX, mouseY, partialTick);
            }
            yOffset += element.getHeight();
        }

        graphics.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int totalContentHeight = POINT_INFO_ELEMENT_HEIGHT * this.points.size();
        int maxScroll = Math.max(0, totalContentHeight - this.getHeight());

        scrollOffset += -scrollY * 10;
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);

        return true;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }
}
