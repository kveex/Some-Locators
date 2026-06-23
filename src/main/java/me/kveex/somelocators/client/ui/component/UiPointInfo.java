package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.screen.LocatorMenuScreen;
import me.kveex.somelocators.client.ui.screen.LocatorPointScreen;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.ChangeTargetPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

//FIXME: Buttons still not working properly, should try to make this class extend Screen instead of Abstract Widget or do this in main screen
public class UiPointInfo extends AbstractWidget {
    private boolean blockElementHoveredFirst = false;
    private final Button renameButton;
    private final LocatorMenuScreen parent;
    private final PointComponent point;
    private final BlockElement blockElement;
    private final LabelElement labelElement;
    private final boolean isTracked;
    private static final int blockSize = 24;
    private static final int MARGIN = 8;
    private static final Identifier TARGETED_FRAME_SPRITE = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "textures/gui/targeted_point_frame.png");
    private static final Identifier TOOLTIP_SPRITE = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "locator_point");

    public UiPointInfo(PointComponent pointComponent, boolean isTracked, LocatorMenuScreen parent, CoordsPair startPos) {
        super(startPos.x(), startPos.y(), 120, 60, Component.empty());
        this.parent = parent;
        this.point = pointComponent;
        this.isTracked = isTracked;
        this.blockElement = new BlockElement(startPos.x(), startPos.y(), blockSize, point.blockState());
        this.labelElement = initLabel();
        this.renameButton = initButton();
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        blockElement.setX(this.getX());
        blockElement.setY(this.getY());

        boolean blockHoveredNow = isMouseOverBlock(mouseX, mouseY);
        if (blockHoveredNow) {
            blockElementHoveredFirst = true;
        }

        // Tooltip
        if (this.isHovered() && TooltipDrawer.canClaim(this.point)) {
            TooltipDrawer.setHoveredWidget(this.point);
            showTooltip(graphics, mouseX, mouseY, delta);
        } else {
            if (TooltipDrawer.isHoveredWidget(this.point)) {
                TooltipDrawer.clearHoveredWidget();
            }
            blockElementHoveredFirst = false;
        }

        blockElement.render(graphics, mouseX, mouseY, delta);
        if (this.isTracked) renderTrackedPointFrame(graphics);
    }

    private void showTooltip(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderTooltipBackground(graphics);
        this.labelElement.render(graphics);
        this.renameButton.render(graphics, mouseX, mouseY, delta);
        if (this.renameButton.isHovered()) {
            SomeLocators.LOGGER.info("button hovered {} {} {} {}", this.renameButton.getX(), this.renameButton.getY(), this.renameButton.getWidth(), this.renameButton.getHeight());
        }
    }

    private Button initButton() {
        int width = 80, height = 20;
        CoordsPair buttonCenter = CoordsPair.create(this.getWidth() + blockSize + 4, 0, width, height);
        return Button.builder(Component.translatable("ui.some_locators.rename_point"), button -> {
            SomeLocators.LOGGER.info("Rename button clicked {} {} {} {}", button.getX(), button.getY(), button.getWidth(), button.getHeight());
            TooltipDrawer.clearHoveredWidget();
            Minecraft.getInstance().setScreen(new LocatorPointScreen(this.point));
        }).bounds(this.getX() + buttonCenter.x(), this.labelElement.getY() + MARGIN * 2, width, height).build();
    }

    private LabelElement initLabel() {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(point.name());
        int fitLabelWidth = blockSize + textWidth + MARGIN;
        if (fitLabelWidth > this.getWidth()) this.setWidth(fitLabelWidth);
        CoordsPair labelCenter = CoordsPair.create(this.getWidth() + blockSize + 4, 0, textWidth, font.lineHeight);
        return new LabelElement(this.getX() + labelCenter.x(), this.getY() + 4, Component.literal(point.name()));
    }

    // Did this, because I need to know if block is hovered, but can't get true information, until block is rendered
    // and I can't draw it first, because tooltip will cover block >:(
    // And that's the fix for blinking tooltip on target point change :D
    private boolean isMouseOverBlock(int mouseX, int mouseY) {
        int x = blockElement.getX();
        int y = blockElement.getY();
        int width = blockElement.getWidth();
        int height = blockElement.getHeight();
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean isHovered() {
        return super.isHovered() && blockElementHoveredFirst;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClicked) {
        // Button check first
        SomeLocators.LOGGER.info("renameButtonNull: {}", this.renameButton == null);
        if (TooltipDrawer.isHoveredWidget(this.point) && this.renameButton != null && this.renameButton.mouseClicked(event, doubleClicked)) {
            return this.renameButton.mouseClicked(event, doubleClicked);
        }

        if (TooltipDrawer.hoveredWidget == null) return false;
        if (this.isTracked) return false;

        int mouseX = (int) event.x();
        int mouseY = (int) event.y();
        if (!isMouseOverBlock(mouseX, mouseY)) return false;

        SomeLocators.LOGGER.info("clicked! double: {}", doubleClicked);
        ChangeTargetPoint changeTargetPoint = new ChangeTargetPoint(this.point);
        changeTargetPoint.send();
        this.parent.setCurrentPoint(this.point);
        SomeLocators.LOGGER.info(TooltipDrawer.hoveredWidget.name());
        TooltipDrawer.clearHoveredWidget();
        return super.mouseClicked(event, doubleClicked);
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
        private static PointComponent hoveredWidget = null;

        public static void setHoveredWidget(PointComponent component) {
            hoveredWidget = component;
        }

        public static boolean isHoveredWidget(PointComponent component) {
            return hoveredWidget == component;
        }

        public static void clearHoveredWidget() {
            hoveredWidget = null;
        }

        public static boolean canClaim(PointComponent component) {
            return hoveredWidget == null || hoveredWidget == component;
        }
    }
}
