package me.kveex.somelocators.client.ui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class LabelElement extends AbstractWidget {
    private final Component text;
    private final Component scrollText;
    private static final Font font = Minecraft.getInstance().font;
    private static final int SCROLL_PART_PADDING = 2;

    public LabelElement(int x, int y, Component text) {
        super(x, y, 0, 0, Component.empty());
        this.width = font.width(text);
        this.height = font.lineHeight;
        this.text = text;
        this.scrollText = Component.empty();
    }

    public LabelElement(int x, int y, int width, Component text) {
        super(x, y, width, font.lineHeight, Component.empty());
        this.text = text;
        this.scrollText = Component.empty();
    }

    public LabelElement(int x, int y, int width, Component staticPart, Component scrolledPart) {
        super(x, y, width, font.lineHeight, Component.empty());
        this.text = staticPart;
        this.scrollText = scrolledPart;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int color = 0xFFFFFFFF;
        graphics.drawString(font, this.text, getX(), getY(), color, true);

        if (!this.scrollText.equals(Component.empty())) {
            int spaceSize = 4;
            int scrollPartStartX = getX() + font.width(this.text) + spaceSize;

            ActiveTextCollector activeTextCollector = graphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE);
            activeTextCollector.acceptScrolling(
                    this.scrollText,
                    scrollPartStartX,
                    scrollPartStartX,
                    this.getX() + this.getWidth() - SCROLL_PART_PADDING,
                    this.getY() - 1,
                    this.getY() + font.lineHeight - 1
            );
        }
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
}