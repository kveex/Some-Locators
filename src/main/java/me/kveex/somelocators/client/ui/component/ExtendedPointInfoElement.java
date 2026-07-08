package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
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
        int labelX = blockElement.getWidth() - BLOCK_MARGIN + TOOLTIP_PADDING;
        LabelElement pointName = new LabelElement(0, 0, this.getWidth() - (labelX + TOOLTIP_PADDING) * 2, Component.translatable("ui.some_locators.point_name"), Component.literal(pointComponent.name()));
        CoordsPair labelCentered = CoordsPair.createCentered(this.getX() + labelX, blockElement.getY(), this.getWidth(), this.getHeight(), pointName.getWidth(), pointName.getHeight());
        pointName.setPosition(labelCentered.x(), this.getY() + TOOLTIP_PADDING);
        pointName.render(graphics);

        // Point Target
        BlockPos pos = pointComponent.target().pos();
        LabelElement pointPosition = new LabelElement(labelCentered.x(), pointName.getY() + LABEL_MARGIN, pointName.getWidth(),
                Component.translatable(
                        "ui.some_locators.point_position",
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                ).withStyle(ChatFormatting.DARK_GRAY)
        );

        pointPosition.render(graphics);

        LabelElement pointDimension = new LabelElement(labelCentered.x(), pointPosition.getY() + LABEL_MARGIN - 2, pointName.getWidth(),
                Component.translatable(
                        "ui.some_locators.point_dimension"
                ).withStyle(ChatFormatting.DARK_GRAY),
                Component.literal(
                        pointComponent.target().dimension().identifier().toString()
                ).withStyle(ChatFormatting.DARK_GRAY)
        );

        pointDimension.render(graphics);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }
}
