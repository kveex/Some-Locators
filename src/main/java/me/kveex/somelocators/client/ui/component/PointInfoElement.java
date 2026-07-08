package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.screen.LocatorMenuScreen;
import me.kveex.somelocators.client.ui.screen.LocatorPointScreen;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.client.ui.util.TooltipDrawer;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.ChangeTargetPointPayload;
import me.kveex.somelocators.network.RemovePointPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class PointInfoElement extends AbstractWidget {
    private Button renameButton;
    private Button removeButton;
    private boolean blockElementHoveredFirst = false;
    private final LocatorMenuScreen parent;
    private final PointComponent point;
    private final BlockElement blockElement;
    private final boolean isTracked;
    private static final int blockSize = 24;
    private static final int MARGIN = 8;
    private static final int fullWidth = 120;
    private static final int fullHeight = 62;
    private static final Identifier TARGETED_FRAME_SPRITE = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "textures/gui/targeted_point_frame.png");
    private static final Font font = Minecraft.getInstance().font;

    public PointInfoElement(PointComponent pointComponent, boolean isTracked, LocatorMenuScreen parent, CoordsPair startPos) {
        super(startPos.x(), startPos.y(), fullWidth, fullHeight, Component.empty());
        this.parent = parent;
        this.point = pointComponent;
        this.isTracked = isTracked;
        this.blockElement = new BlockElement(startPos.x(), startPos.y(), blockSize, point.blockState());
        this.active = false;
        int textWidth = font.width(point.name());
        int adaptedWidgetWidth = Math.max(blockSize + textWidth + MARGIN, fullWidth);
        this.setWidth(adaptedWidgetWidth);
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        blockElement.setX(this.getX());
        blockElement.setY(this.getY());

        boolean blockHoveredNow = isMouseOverBlock(mouseX, mouseY);
        if (blockHoveredNow) {
            blockElementHoveredFirst = true;
        }

        if (this.isHovered() && TooltipDrawer.canClaim(this)) {
            TooltipDrawer.setHoveredWidget(this);
            this.active = true;
            showTooltip(graphics, mouseX, mouseY, delta);
        } else {
            if (TooltipDrawer.isHoveredWidget(this)) {
                TooltipDrawer.clearHoveredWidget();
            }
            blockElementHoveredFirst = false;
            this.active = false;
        }

        blockElement.render(graphics, mouseX, mouseY, delta);
        if (this.isTracked) renderTrackedPointFrame(graphics);
    }

    private void showTooltip(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Tooltip
        TooltipDrawer.renderTooltipBackground(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        // Label
        int labelMargin = 4;
        CoordsPair labelCenter = CoordsPair.create(this.getWidth() - blockSize - labelMargin, 0, font.width(point.name()), font.lineHeight);
        LabelElement labelElement = new LabelElement(this.getX() + labelCenter.x() + blockSize + labelMargin, this.getY() + labelMargin, Component.literal(point.name()));
        labelElement.render(graphics);

        int buttonWidth = 80, buttonHeight = 20, buttonMargin = 2;
        CoordsPair buttonCenter = CoordsPair.create(this.getWidth() + blockSize + labelMargin, 0, buttonWidth, buttonHeight);
        int buttonX = this.getX() + buttonCenter.x(), renameButtonY = labelElement.getY() + MARGIN * 2;

        // Rename button
        if (this.renameButton == null) {
            this.renameButton = Button.builder(Component.translatable("ui.some_locators.rename_point"), button -> {
                TooltipDrawer.clearHoveredWidget();
                Minecraft.getInstance().setScreen(new LocatorPointScreen(this.point));
            }).bounds(buttonX, renameButtonY, buttonWidth, buttonHeight).build();
        }
        this.renameButton.render(graphics, mouseX, mouseY, delta);

        // Remove button
        int removeButtonY = renameButtonY + buttonHeight + buttonMargin;
        if (this.removeButton == null) {
            this.removeButton = Button.builder(Component.translatable("ui.some_locators.remove_point"), button -> {
                TooltipDrawer.clearHoveredWidget();
                RemovePointPayload removePoint = new RemovePointPayload(this.point);
                removePoint.send();

                this.parent.removePoint(this.point);
            }).bounds(buttonX, removeButtonY, buttonWidth, buttonHeight).build();
        }
        this.removeButton.render(graphics, mouseX, mouseY, delta);
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
        if (!TooltipDrawer.isHoveredWidget(this)) return false;

        if (this.renameButton.mouseClicked(event, doubleClicked)) {
            return super.mouseClicked(event, doubleClicked);
        }

        if (this.removeButton.mouseClicked(event, doubleClicked)) {
            return super.mouseClicked(event, doubleClicked);
        }

        if (this.isTracked) return false;

        int mouseX = (int) event.x();
        int mouseY = (int) event.y();
        if (!isMouseOverBlock(mouseX, mouseY)) return false;

        ChangeTargetPointPayload changeTargetPoint = new ChangeTargetPointPayload(this.point);
        changeTargetPoint.send();

        this.parent.setCurrentPoint(this.point);

        TooltipDrawer.clearHoveredWidget();

        return super.mouseClicked(event, doubleClicked);
    }



    private void renderTrackedPointFrame(@NonNull GuiGraphics graphics) {
        int u = 0, v = 0;
        int offset = 6, cancelOffset = offset * 2;
        int x = blockElement.getX() - MARGIN + offset, y = blockElement.getY() - MARGIN + offset;
        int width = blockElement.getWidth() + MARGIN * 2 - cancelOffset, height = blockElement.getHeight() + MARGIN * 2 - cancelOffset;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TARGETED_FRAME_SPRITE, x, y, u, v, width, height, width, height);
    }


}
