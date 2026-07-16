package me.kveex.somelocators.client.ui.element;

import me.kveex.somelocators.SomeLocators;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

public class ButtonElement extends AbstractButton {
    private boolean isPressed;
    private final OnPress onPress;
    private final LabelElement buttonText;
    private final long holdDurationMs;
    private long pressStartMs = -1;
    private boolean actionFired = false;
    private int buttonTextOffset = 2;
    private static final int BORDER_COLOR_EMPTY = 0xFF560319;
    private final int borderColorProgress;
    private static final int BUTTON_TEXT_MARGIN = 2;
    private static final Identifier BUTTON = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "widget/redstone_button");
    private static final Identifier BUTTON_DISABLED = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "widget/redstone_button_disabled");
    private static final Identifier BUTTON_HOVERED = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "widget/redstone_button_hover");
    private static final Identifier BUTTON_PRESSED = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "widget/redstone_button_press");

    private ButtonElement(int x, int y, int width, int height, Component message, OnPress onPress, long holdDurationMs, int borderColorProgress) {
        super(
                x + (holdDurationMs > 0 ? 1 : 0),
                y + (holdDurationMs > 0 ? 1 : 0),
                width  - (holdDurationMs > 0 ? 2 : 0),
                height - (holdDurationMs > 0 ? 2 : 0),
                message
        );
        this.onPress = onPress;
        this.holdDurationMs = holdDurationMs;
        this.buttonText = LabelElement.builder(this.getX() + BUTTON_TEXT_MARGIN, 0, this.getMessage())
                .centered()
                .width(this.getWidth() - BUTTON_TEXT_MARGIN * 2)
                .build();
        this.borderColorProgress = borderColorProgress;
    }

    private Identifier resolveSprite() {
        if (this.isPressed)    return BUTTON_PRESSED;
        if (!this.isActive())  return BUTTON_DISABLED;
        if (this.isHovered())  return BUTTON_HOVERED;
        return BUTTON;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        if (holdDurationMs > 0) {
            this.isPressed = true;
            this.buttonTextOffset = 0;
            this.pressStartMs = Util.getMillis();
            this.actionFired = false;
        } else {
            this.isPressed = true;
            this.buttonTextOffset = 0;
            this.onPress.onPress(this);
        }
    }

    @Override
    public void onRelease(@NonNull MouseButtonEvent event) {
        this.isPressed = false;
        this.buttonTextOffset = 2;
        this.pressStartMs = -1;
        this.actionFired = false;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {}

    @Override
    protected void renderContents(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (holdDurationMs > 0) {
            float progress = getHoldProgress();

            if (progress >= 1f && !actionFired) {
                actionFired = true;
                isPressed = false;
                buttonTextOffset = 2;
                pressStartMs = -1;
                this.onPress.onPress(this);
            }

            renderProgressBorder(graphics, progress);
        }

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                resolveSprite(),
                this.getX(), this.getY(),
                this.getWidth(), this.getHeight()
        );

        int buttonTextY = this.getY()
                + (this.getHeight() / 2 - Minecraft.getInstance().font.lineHeight / 2)
                - buttonTextOffset;
        buttonText.setY(buttonTextY);
        buttonText.render(graphics);
    }

    private float getHoldProgress() {
        if (!isPressed || pressStartMs < 0 || holdDurationMs <= 0) return 0f;
        long elapsed = Util.getMillis() - pressStartMs;
        return Math.min(1f, (float) elapsed / holdDurationMs);
    }

    private void renderProgressBorder(GuiGraphics graphics, float progress) {
        int x = this.getX() - 1;
        int y = this.getY() - 1;
        int w = this.getWidth() + 2;
        int h = this.getHeight() + 2;

        graphics.renderOutline(x, y, w, h, BORDER_COLOR_EMPTY);
        if (progress <= 0f) return;

        int filledX = Math.min(x + (int)(w * progress), x + w);

        graphics.fill(x, y, filledX, y + 1, borderColorProgress);
        graphics.fill(x, y + h - 1, filledX, y + h, borderColorProgress);
        graphics.fill(x, y, x + 1, y + h, borderColorProgress);
        if (progress >= 1f) {
            graphics.fill(x + w - 1, y, x + w, y + h, borderColorProgress);
        }
    }

    public void active(boolean active) {
        this.active = active;
    }

    public static Builder builder(Component text, ButtonElement.OnPress onPress) {
        return new Builder(text, onPress);
    }

    public static class Builder {
        private final Component text;
        private final ButtonElement.OnPress onPress;
        private int x;
        private int y;
        private int width = 150;
        private long holdDurationMs = 0;
        private int borderColorProgress = 0xFFFFFFFF;

        public Builder(Component text, ButtonElement.OnPress onPress) {
            this.text = text;
            this.onPress = onPress;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder ticksAmountForPress(int ticks, int borderColorProgress) {
            this.holdDurationMs = ticks * 50L;
            this.borderColorProgress = borderColorProgress;
            return this;
        }

        public ButtonElement build() {
            int height = 20;
            return new ButtonElement(
                    this.x, this.y,
                    this.width, height,
                    this.text, this.onPress,
                    this.holdDurationMs, this.borderColorProgress
            );
        }
    }

    @Environment(EnvType.CLIENT)
    public interface OnPress {
        void onPress(ButtonElement button);
    }
}