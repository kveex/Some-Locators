package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.TooltipDrawer;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ExtendedPointInfoListComponent extends AbstractWidget {
    private final List<PointComponent> points;
    private double scrollOffset;
    private static final int POINT_INFO_ELEMENT_HEIGHT = 40;
    private final List<ExtendedPointInfoElement> elements;

    public ExtendedPointInfoListComponent(int x, int y, int width, int height, List<PointComponent> points, boolean hideInfo) {
        super(x, y, width, height, Component.empty());
        this.points = points;
        this.elements = points.stream()
                .map(point -> new ExtendedPointInfoElement(this.getX(), 0, this.getWidth(), POINT_INFO_ELEMENT_HEIGHT, point, hideInfo))
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

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        return false;
    }

    private static class ExtendedPointInfoElement extends AbstractWidget {
        private final PointComponent pointComponent;
        private final boolean hideInfo;
        private static final int BLOCK_MARGIN = 16;
        private static final int LABEL_MARGIN = 12;
        private static final int TOOLTIP_PADDING = 4;

        public ExtendedPointInfoElement(int x, int y, int width, int height, PointComponent pointComponent, boolean hideInfo) {
            super(x, y, width, height, Component.empty());
            this.pointComponent = pointComponent;
            this.hideInfo = hideInfo;
        }

        @Override
        protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            TooltipDrawer.renderTooltipBackground(graphics, this.getX() + TOOLTIP_PADDING, this.getY() + TOOLTIP_PADDING, this.getWidth() - TOOLTIP_PADDING * 2, this.getHeight() - TOOLTIP_PADDING * 2);
            // Block
            int blockElementSize = 32, blockElementX = this.getX() + TOOLTIP_PADDING;
                BlockElement blockElement = new BlockElement(blockElementX, this.getY() + TOOLTIP_PADDING, blockElementSize, pointComponent.blockState());
                blockElement.render(graphics, mouseX, mouseY, partialTick);

            // Point Name
            int pointNameX = blockElementX + blockElementSize + TOOLTIP_PADDING;
            LabelElement pointName = LabelElement.builder(pointNameX, this.getY() + TOOLTIP_PADDING + 1, Component.literal(pointComponent.name()))
                    .width(this.getWidth() - BLOCK_MARGIN * 3)
                    .staticText(Component.translatable("ui.some_locators.point_name"))
                    .build();
            pointName.render(graphics);

            // Point Target
            BlockPos pos = pointComponent.target().pos();
            MutableComponent pointPositionText = Component.translatable("ui.some_locators.point_position", pos.getX(), pos.getY(), pos.getZ())
                    .withStyle(ChatFormatting.DARK_GRAY);

            if (this.hideInfo) pointPositionText.withStyle(ChatFormatting.OBFUSCATED);

            LabelElement pointPosition = LabelElement.builder(pointName.getX(), pointName.getY() + LABEL_MARGIN, pointPositionText)
                    .width(pointName.getWidth())
                    .build();

            pointPosition.render(graphics);

            Component dimensionText = Component.translatable("ui.some_locators.point_dimension").withStyle(ChatFormatting.DARK_GRAY);
            MutableComponent dimensionIdentifier = Component.literal(pointComponent.target().dimension().identifier().toString())
                    .withStyle(ChatFormatting.DARK_GRAY);

            if (this.hideInfo) dimensionIdentifier.withStyle(ChatFormatting.OBFUSCATED);

            LabelElement pointDimension = LabelElement.builder(pointName.getX(), pointPosition.getY() + LABEL_MARGIN - 2, dimensionIdentifier)
                    .staticText(dimensionText)
                    .width(pointName.getWidth())
                    .build();

            pointDimension.render(graphics);
        }

        @Override
        protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

        }
    }
}
