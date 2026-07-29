package me.kveex.somelocators.client.ui.component;

import commonnetwork.api.Dispatcher;
import me.kveex.somelocators.Constants;
import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.ButtonElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.screen.locator.LocatorMenuScreen;
import me.kveex.somelocators.client.ui.screen.locator.LocatorPointScreen;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.client.ui.util.TooltipDrawer;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.locator.ChangeTargetPointPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class PointInfoComponent extends AbstractWidget {
    private ButtonElement renameButton;
    private ButtonElement removeButton;
    private boolean blockElementHoveredFirst = false;
    private final LocatorMenuScreen parent;
    private final PointComponent point;
    private final BlockElement blockElement;
    private final boolean isTracked;
    private static final int blockSize = 24;
    private static final int MARGIN = 8;
    private static final int fullWidth = 120;
    private static final int fullHeight = 62;
    private static final Identifier TARGETED_FRAME_SPRITE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/targeted_point_frame.png");
    private final int page;

    public PointInfoComponent(PointComponent pointComponent, boolean isTracked, LocatorMenuScreen parent, CoordsPair startPos, int page) {
        super(startPos.x(), startPos.y(), fullWidth, fullHeight, Component.empty());
        this.parent = parent;
        this.point = pointComponent;
        this.isTracked = isTracked;
        this.blockElement = new BlockElement(startPos.x(), startPos.y(), blockSize, point.blockState());
        this.active = false;
        this.page = page;
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
        int labelX = this.getX() + blockSize + labelMargin, labelY = this.getY() + labelMargin;
        int labelWidth = fullWidth - blockSize - labelMargin;
        LabelElement labelElement = LabelElement.builder(labelX, labelY, Component.literal(point.name()))
                .width(labelWidth)
                .centered()
                .build();

        labelElement.render(graphics);

        int buttonWidth = 82, buttonHeight = 20, buttonMargin = 2;
        int buttonX = CoordsPair.centeredX(this.getX(), this.getWidth() + blockSize + labelMargin, buttonWidth);
        int renameButtonY = labelElement.getY() + MARGIN * 2;

        // Rename button
        if (this.renameButton == null) {
            this.renameButton = ButtonElement.builder(Component.translatable("ui.some_locators.rename_point"), button -> {
                TooltipDrawer.clearHoveredWidget();
                Minecraft.getInstance().setScreen(new LocatorPointScreen(this.point, page));
            }).pos(buttonX, renameButtonY).width(buttonWidth).build();
        }
        this.renameButton.render(graphics, mouseX, mouseY, delta);

        // Remove button
        int removeButtonY = renameButtonY + buttonHeight + buttonMargin;
        if (this.removeButton == null) {
            this.removeButton = ButtonElement.builder(Component.translatable("ui.some_locators.remove_point"), button -> this.parent.scheduleAction(() -> {
                TooltipDrawer.clearHoveredWidget();
                this.parent.removePoint(this.point);
            })).ticksAmountForPress(20, 0xFFFFD700).pos(buttonX, removeButtonY).width(buttonWidth).build();
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
            return true;
        }

        if (this.removeButton.mouseClicked(event, doubleClicked)) {
            return true;
        }

        if (this.isTracked) return false;

        int mouseX = (int) event.x();
        int mouseY = (int) event.y();
        if (!isMouseOverBlock(mouseX, mouseY)) return false;

        ChangeTargetPointPayload changeTargetPoint = new ChangeTargetPointPayload(this.point);
        Dispatcher.sendToServer(changeTargetPoint);

        this.parent.setCurrentPoint(this.point);

        TooltipDrawer.clearHoveredWidget();

        return super.mouseClicked(event, doubleClicked);
    }

    @Override
    public void onRelease(@NonNull MouseButtonEvent event) {
        if (this.removeButton.mouseReleased(event)) {
            this.removeButton.onRelease(event);
        }
    }

    private void renderTrackedPointFrame(@NonNull GuiGraphics graphics) {
        int u = 0, v = 0;
        int offset = 6, cancelOffset = offset * 2;
        int x = blockElement.getX() - MARGIN + offset, y = blockElement.getY() - MARGIN + offset;
        int width = blockElement.getWidth() + MARGIN * 2 - cancelOffset, height = blockElement.getHeight() + MARGIN * 2 - cancelOffset;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TARGETED_FRAME_SPRITE, x, y, u, v, width, height, width, height);
    }


}
