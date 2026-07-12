package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.TooltipDrawer;
import me.kveex.somelocators.component.PointComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SelectablePointListComponent extends AbstractWidget {
    private static final int FONT_HEIGHT = Minecraft.getInstance().font.lineHeight;
    private static final int ELEMENT_HEIGHT = 32;
    private final OnPress onPress;
    private double selectableScrollOffset;
    private double selectedScrollOffset;
    private final List<SelectablePointInfoElement> selectable;
    private final List<SelectablePointInfoElement> selected;

    public SelectablePointListComponent(int x, int y, int width, int height, List<PointComponent> points, OnPress onPress) {
        super(x, y, width, height, Component.empty());
        this.selectable = new ArrayList<>(points.stream()
                .map(point -> new SelectablePointInfoElement(x, 0, width / 2, ELEMENT_HEIGHT, point))
                .toList());
        this.selected = new ArrayList<>();
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(
                this.getX(), this.getY(),
                this.getX() + this.getWidth(), this.getY() + this.getHeight()
        );

        int halfWidth = this.getWidth() / 2;

        renderElements(graphics, mouseX, mouseY, partialTick, this.selectableScrollOffset, this.selectable, this.getX());
        renderElements(graphics, mouseX, mouseY, partialTick, this.selectedScrollOffset, this.selected, this.getX() + halfWidth);

        graphics.disableScissor();
    }

    private void renderElements(GuiGraphics graphics, int mouseX, int mouseY,
            float partialTick, double scrollOffset,
            List<SelectablePointInfoElement> elements, int startX) {
        double yOffset = this.getY() - scrollOffset;

        for (SelectablePointInfoElement element : elements) {
            if (yOffset + element.getHeight() > this.getY() && yOffset < this.getY() + this.getHeight()) {
                element.setX(startX);
                element.setY((int) yOffset);
                element.render(graphics, mouseX, mouseY, partialTick);
            }
            yOffset += element.getHeight();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int midX = this.getX() + this.getWidth() / 2;

        if (mouseX < midX) {
            this.selectableScrollOffset = calcScrollOffset(
                    this.selectableScrollOffset, scrollY, this.selectable.size());
        } else {
            this.selectedScrollOffset = calcScrollOffset(
                    this.selectedScrollOffset, scrollY, this.selected.size());
        }

        return true;
    }

    private double calcScrollOffset(double currentOffset, double scrollY, int listSize) {
        int totalContentHeight = ELEMENT_HEIGHT * listSize;
        int maxScroll = Math.max(0, totalContentHeight - this.getHeight());
        return Math.clamp(currentOffset + -scrollY * 10, 0, maxScroll);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        int midX = this.getX() + this.getWidth() / 2;

        if (event.x() < midX) {
            return click(event.y(), this.selectableScrollOffset,
                    this.selectable, ListSide.SELECTABLE);
        } else {
            return click(event.y(), this.selectedScrollOffset,
                    this.selected, ListSide.SELECTED);
        }
    }

    private boolean click(double mouseY, double scrollOffset,
            List<SelectablePointInfoElement> list, ListSide side) {
        int index = (int) ((mouseY - this.getY() + scrollOffset) / ELEMENT_HEIGHT);

        if (index >= 0 && index < list.size()) {
            moveToOtherList(side, index);
            this.onPress.onPress(this);
        }

        return true;
    }

    private void moveToOtherList(ListSide side, int index) {
        switch (side) {
            case SELECTABLE -> {
                SelectablePointInfoElement element = this.selectable.get(index);
                this.selectable.remove(index);
                this.selected.add(element);
                this.selectableScrollOffset = calcScrollOffset(
                        this.selectableScrollOffset, 0, this.selectable.size());
            }
            case SELECTED -> {
                SelectablePointInfoElement element = this.selected.get(index);
                this.selected.remove(index);
                this.selectable.add(element);
                this.selectedScrollOffset = calcScrollOffset(
                        this.selectedScrollOffset, 0, this.selected.size());
            }
        }
    }

    public void moveAllToSelected() {
        this.selected.addAll(this.selectable);
        this.selectable.clear();
        this.selectableScrollOffset = 0;
        this.onPress.onPress(this);
    }

    public void moveAllToSelectable() {
        this.selectable.addAll(this.selected);
        this.selected.clear();
        this.selectedScrollOffset = 0;
        this.onPress.onPress(this);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {}

    public List<PointComponent> getSelectedPoints() {
        return this.selected.stream()
                .map(SelectablePointInfoElement::getPoint)
                .toList();
    }

    private enum ListSide {
        SELECTABLE, SELECTED
    }

    public static class SelectablePointInfoElement extends AbstractWidget {
        private final PointComponent point;
        private final BlockElement block;
        private final LabelElement pointName;
        private static final int TOOLTIP_PADDING = 4;
        private static final int BLOCK_SIZE = 24;

        public SelectablePointInfoElement(int x, int y, int width, int height, PointComponent point) {
            super(x, y, width, height, Component.empty());
            this.point = point;
            this.block = new BlockElement(
                    x + TOOLTIP_PADDING, y + TOOLTIP_PADDING,
                    BLOCK_SIZE, point.blockState()
            );
            this.pointName = LabelElement.builder(0, 0, Component.literal(point.name()))
                    .centered()
                    .width(width - BLOCK_SIZE * 2 + TOOLTIP_PADDING)
                    .build();
        }

        @Override
        protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            TooltipDrawer.renderTooltipBackground(
                    graphics,
                    this.getX() + TOOLTIP_PADDING,
                    this.getY() + TOOLTIP_PADDING,
                    this.getWidth() - TOOLTIP_PADDING * 2,
                    this.getHeight() - TOOLTIP_PADDING * 2
            );

            block.setX(this.getX() + TOOLTIP_PADDING);
            block.setY(this.getY() + TOOLTIP_PADDING);
            block.render(graphics, mouseX, mouseY, partialTick);

            pointName.setPosition(
                    block.getX() + block.getWidth() + TOOLTIP_PADDING,
                    this.getY() + this.getHeight() / 2 - FONT_HEIGHT
            );
            pointName.render(graphics);
        }

        @Override
        protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {}

        public PointComponent getPoint() {
            return point;
        }
    }

    @Environment(EnvType.CLIENT)
    public interface OnPress {
        void onPress(SelectablePointListComponent component);
    }
}