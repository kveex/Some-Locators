package me.kveex.somelocators.client.ui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class TextInputElement extends EditBox {
    public static final Font font = Minecraft.getInstance().font;
    public static final int MAX_FONT_HINT_LENGTH = 110;
    public TextInputElement(int x, int y, int width, int height, Component fromBlockName) {
        super(font, x, y, width, height, Component.empty());
        this.setMaxLength(32);
        this.fitHint(fromBlockName);
    }

    private void fitHint(Component text) {
        String textString = text.getString();
        int hintFontWidth = font.width(textString);

        if (hintFontWidth >= MAX_FONT_HINT_LENGTH) {
            int maxHintSymbolsLength = 18;
            textString = textString.substring(0, maxHintSymbolsLength) + "...";
        }

        this.setHint(Component.literal(textString));
    }
}
