package me.kveex.somelocators.client.ui.component;

import me.kveex.somelocators.client.ui.screen.LocatorMenuScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class PageSwitchElement extends AbstractWidget {
    private final LocatorMenuScreen parent;
    public final int startSwitchLimit;
    public final int endSwitchLimit;
    private static final Identifier REDSTONE_TORCH_SPRITE = Identifier.withDefaultNamespace("textures/block/redstone_torch_off.png");
    private final SwitchWidget switchWidget;

    public PageSwitchElement(int x, int y, LocatorMenuScreen parent) {
        super(x, y, 90, 36, Component.empty());
        this.parent = parent;
        startSwitchLimit = x;
        endSwitchLimit = x + 72;
        switchWidget = new SwitchWidget(x, y);
    }

    @Override
    public void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
//        graphics.renderOutline(this.getX() - 1, this.getY() - 1, this.getWidth() + 2, this.getHeight() + 2, 0xFFFFFFFF);
        switchWidget.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    private int getSnapX(int currentX) {
        int[] snapPoints = { startSwitchLimit, startSwitchLimit + 12, startSwitchLimit + 24, startSwitchLimit + 36, startSwitchLimit + 48, startSwitchLimit + 60, endSwitchLimit };

        int closestIndex = 0;
        int closestDistance = Math.abs(currentX - snapPoints[0]);

        for (int i = 1; i < this.parent.getPagesAmount(); i++) {
            int distance = Math.abs(currentX - snapPoints[i]);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIndex = i;
            }
        }

        this.parent.setCurrentPage(closestIndex);
        return snapPoints[closestIndex];
    }

    @Override
    protected void onDrag(@NonNull MouseButtonEvent event, double mouseX, double mouseY) {
        if (switchWidget.mouseDragged(event, mouseX, mouseY)) {
            switchWidget.onDrag(event, mouseX, mouseY);
        }
    }

    @Override
    public void onRelease(@NonNull MouseButtonEvent event) {
        int snappedX = getSnapX((int) (event.x() - (double) this.switchWidget.getWidth() / 2));
        this.switchWidget.setX(snappedX);
    }

    private class SwitchWidget extends AbstractWidget {
        public SwitchWidget(int x, int y) {
            super(x, y, 18, 36, Component.empty());
        }

        @Override
        protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int sizeMultiplier = 9, size = 16, textureSize = size * sizeMultiplier;
            int textureCutAmount = 12 * sizeMultiplier;
            graphics.blit(RenderPipelines.GUI_TEXTURED, REDSTONE_TORCH_SPRITE, this.getX(), this.getY(), 7 * sizeMultiplier, 6 * sizeMultiplier, textureSize, textureSize - textureCutAmount, textureSize, textureSize);
        }

        @Override
        protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

        }

        @Override
        protected void onDrag(@NonNull MouseButtonEvent event, double mouseX, double mouseY) {
            int nextX = ((int) (event.x() - (double) this.getWidth() / 2));
            if (nextX <= startSwitchLimit) {
                this.setX(startSwitchLimit);
            }

            if (nextX >= endSwitchLimit) {
                this.setX(endSwitchLimit);
            }

            if (nextX < startSwitchLimit || nextX > endSwitchLimit) return;

            this.setX(nextX);
        }
    }
}
