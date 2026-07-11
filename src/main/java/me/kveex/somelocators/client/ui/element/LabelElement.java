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
    private final Component staticText;
    private final Component scrollText;
    private final boolean centered;
    private static final Font font = Minecraft.getInstance().font;

    private LabelElement(Builder builder) {
        super(builder.x, builder.y, builder.width, font.lineHeight, Component.empty());
        this.scrollText = builder.scrollText;
        this.staticText = builder.staticText;
        this.centered = builder.centered;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int color = 0xFFFFFFFF;
        int spaceSize = 4;
        int scrollPartStartX;

        ActiveTextCollector activeTextCollector = graphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE);

        if (!this.staticText.equals(Component.empty())) {
            scrollPartStartX = this.getX() + font.width(this.staticText) + spaceSize;
            graphics.drawString(font, this.staticText, this.getX(), this.getY(), color, true);
        } else {
            scrollPartStartX = this.getX();
        }

        int maxX = this.getX() + this.getWidth();
        int center = centered ? (scrollPartStartX + maxX) / 2 : scrollPartStartX;

        activeTextCollector.acceptScrolling(
                this.scrollText,
                center,
                scrollPartStartX,
                maxX,
                this.getY() - 1,
                this.getY() + font.lineHeight - 1
        );
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

    public static Builder builder(int x, int y, Component scrollText) {
        return new Builder(x, y, scrollText);
    }

    public static class Builder {
        private final int x;
        private final int y;
        private final Component scrollText;
        private Component staticText = Component.empty();
        private boolean centered = false;
        private int width;

        public Builder(int x, int y, Component scrollText) {
            this.x = x;
            this.y = y;
            this.scrollText = scrollText;
            this.width = font.width(scrollText);
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder centered(boolean centered) {
            this.centered = centered;
            return this;
        }

        public Builder staticText(Component staticText) {
            this.staticText = staticText;
            return this;
        }

        public LabelElement build() {
            return new LabelElement(this);
        }
    }
}