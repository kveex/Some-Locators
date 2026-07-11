package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.TooltipDrawer;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ExtendedPointInfoElement extends AbstractWidget {
    private final PointComponent pointComponent;
    private static final int BLOCK_MARGIN = 16;
    private static final int LABEL_MARGIN = 12;
    private static final int TOOLTIP_PADDING = 4;
    public ExtendedPointInfoElement(int x, int y, int width, int height, PointComponent pointComponent) {
        super(x, y, width, height, Component.empty());
        this.pointComponent = pointComponent;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        TooltipDrawer.renderTooltipBackground(graphics, this.getX() + TOOLTIP_PADDING, this.getY() + TOOLTIP_PADDING, this.getWidth() - TOOLTIP_PADDING * 2, this.getHeight() - TOOLTIP_PADDING * 2);
        // Block
        BlockElement blockElement = new BlockElement(this.getX() + TOOLTIP_PADDING, this.getY() + TOOLTIP_PADDING, 32, pointComponent.blockState(), true);
        blockElement.render(graphics, mouseX, mouseY, partialTick);

        // Point Name
        int pointNameX = blockElement.getX() + blockElement.getWidth() + TOOLTIP_PADDING;
        LabelElement pointName = LabelElement.builder(pointNameX, this.getY() + TOOLTIP_PADDING + 1, Component.literal(pointComponent.name()))
                .width(this.getWidth() - BLOCK_MARGIN * 3)
                .staticText(Component.translatable("ui.some_locators.point_name"))
                .build();
        pointName.render(graphics);

        // Point Target
        BlockPos pos = pointComponent.target().pos();
        Component pointPositionText = Component.translatable("ui.some_locators.point_position", pos.getX(), pos.getY(), pos.getZ())
                .withStyle(ChatFormatting.DARK_GRAY);

        LabelElement pointPosition = LabelElement.builder(pointName.getX(), pointName.getY() + LABEL_MARGIN, pointPositionText)
                .width(pointName.getWidth())
                .build();

        pointPosition.render(graphics);

        Component dimensionText = Component.translatable("ui.some_locators.point_dimension").withStyle(ChatFormatting.DARK_GRAY);
        Component dimensionIdentifier = Component.literal(pointComponent.target().dimension().identifier().toString())
                .withStyle(ChatFormatting.DARK_GRAY);

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
