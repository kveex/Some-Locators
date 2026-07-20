package me.kveex.somelocators.client.ui.element;

import me.kveex.somelocators.client.ui.util.TooltipDrawer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

public class EntityElement extends AbstractWidget {
    private final int scale;
    private final float lookAngleX;
    private final float lookAngleY;
    private final LivingEntity entity;
    private final float yOffset;

    private EntityElement(Builder builder) {
        super(builder.x, builder.y, builder.width, builder.height, Component.empty());
        this.scale = builder.scale;
        this.entity = builder.entity;
        this.lookAngleX = builder.lookAngleX;
        this.lookAngleY = builder.lookAngleY;
        this.yOffset = builder.yOffset;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        TooltipDrawer.renderTooltipBackground(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        int centerX = this.getX() + this.getWidth() / 2;
        int centerY = this.getY() + this.getHeight() / 2;

        this.entity.tickCount = (int) (Util.getMillis() / 50L);
        this.entity.setCustomNameVisible(false);

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                this.getX(), this.getY(),
                this.getX() + this.getWidth(),
                this.getY() + this.getHeight(),
                this.scale, this.yOffset,
                centerX + this.lookAngleX,
                centerY + this.lookAngleY,
                this.entity
        );
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        return false;
    }

    public static Builder builder(int x, int y, LivingEntity entity) {
        return new Builder(x, y, entity);
    }

    public static class Builder {
        private final int x;
        private final int y;
        private final LivingEntity entity;
        private int width = 50;
        private int height = 80;
        private float lookAngleX = 0f;
        private float lookAngleY = 0f;
        private int scale = 30;
        private float yOffset = 0f;

        public Builder(int x, int y, LivingEntity entity) {
            this.x = x;
            this.y = y;
            this.entity = entity;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder scale(int scale) {
            this.scale = scale;
            return this;
        }

        public Builder lookAngle(float lookAngleX, float lookAngleY) {
            this.lookAngleX = lookAngleX;
            this.lookAngleY = lookAngleY;
            return this;
        }

        public Builder yOffset(float yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        public EntityElement build() {
            return new EntityElement(this);
        }
    }
}
