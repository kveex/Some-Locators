package me.kveex.somelocators.client.ui.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

public class ItemElement extends AbstractWidget {
    private final ItemStack itemStack;
    private final int scale;

    public ItemElement(int x, int y, int scale, ItemStack itemStack) {
        super(x, y, scale * 16, scale * 16, Component.empty());
        this.itemStack = itemStack;
        this.scale = scale;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Matrix3x2fStack pose = graphics.pose();

        pose.pushMatrix();

        pose.translate(this.getX(), this.getY());
        pose.scale(scale);

        graphics.renderFakeItem(itemStack, 0, 0);

        pose.popMatrix();
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        return false;
    }
}
