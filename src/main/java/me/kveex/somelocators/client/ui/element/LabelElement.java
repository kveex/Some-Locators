package me.kveex.somelocators.client.ui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class LabelElement extends AbstractWidget {
    private final Component text;
    private static final Font font = Minecraft.getInstance().font;
    private int color = 0xFFFFFFFF;

    public LabelElement(int x, int y, Component text) {
        super(x, y, 0, 0, Component.empty());
        this.width = font.width(text);
        this.height = font.lineHeight;
        this.text = text;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.drawString(font, this.text, getX(), getY(), color, true);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {
    }

    public void render(@NonNull GuiGraphics graphics) {
        this.render(graphics, 0, 0, 0);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        return false;
    }

    public void setColor(int color) {
        this.color = color;
    }
}