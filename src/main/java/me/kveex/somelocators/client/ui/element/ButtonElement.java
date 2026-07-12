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
import org.jspecify.annotations.NonNull;

public class ButtonElement extends AbstractButton {
    private boolean isPressed;
    private final OnPress onPress;
    private final LabelElement buttonText;
    private int buttonTextOffset = 2;
    private static final int buttonTextMargin = 2;
    private static final Identifier BUTTON = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "widget/redstone_button");
    private static final Identifier BUTTON_DISABLED = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "widget/redstone_button_disabled");
    private static final Identifier BUTTON_HOVERED = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "widget/redstone_button_hover");
    private static final Identifier BUTTON_PRESSED = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "widget/redstone_button_press");

    public ButtonElement(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
        this.buttonText = LabelElement.builder(this.getX() + buttonTextMargin, 0, this.getMessage())
                .centered()
                .width(width - buttonTextMargin * 2)
                .build();
    }

    private Identifier resolveSprite() {
        if (this.isPressed) return BUTTON_PRESSED;
        if (!this.isActive()) return BUTTON_DISABLED;
        if (this.isHovered()) return BUTTON_HOVERED;
        return BUTTON;
    }

    @Override
    public void onPress(@NonNull InputWithModifiers input) {
        this.isPressed = true;
        this.buttonTextOffset = 0;
        this.onPress.onPress(this);
    }

    @Override
    public void onRelease(@NonNull MouseButtonEvent event) {
        this.isPressed = false;
        this.buttonTextOffset = 2;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected void renderContents(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Identifier sprite = resolveSprite();
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                sprite,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight()
        );
//        this.renderDefaultLabel(graphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
        int buttonTextY = this.getY() + (this.getHeight() / 2 - Minecraft.getInstance().font.lineHeight / 2) - buttonTextOffset;
        buttonText.setY(buttonTextY);
        buttonText.render(graphics);
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

        public ButtonElement build() {
            int height = 20;
            return new ButtonElement(
                    this.x, this.y,
                    this.width, height,
                    this.text, this.onPress
            );
        }
    }

    @Environment(EnvType.CLIENT)
    public interface OnPress {
        void onPress(ButtonElement button);
    }
}
