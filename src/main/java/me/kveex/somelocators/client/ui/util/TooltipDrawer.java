package me.kveex.somelocators.client.ui.util;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.component.PointInfoElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class TooltipDrawer {
    private static final Identifier TOOLTIP_SPRITE = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "locator_point");
    private static PointInfoElement hoveredWidget = null;

    public static void renderTooltipBackground(@NonNull GuiGraphics graphics, int x, int y, int width, int height) {
        TooltipRenderUtil.renderTooltipBackground(graphics, x, y, width, height, TOOLTIP_SPRITE);
    }

    public static void setHoveredWidget(PointInfoElement pointInfo) {
        hoveredWidget = pointInfo;
    }

    public static boolean isHoveredWidget(PointInfoElement pointInfo) {
        return hoveredWidget == pointInfo;
    }

    public static void clearHoveredWidget() {
        hoveredWidget = null;
    }

    public static boolean canClaim(PointInfoElement pointInfo) {
        return hoveredWidget == null || hoveredWidget == pointInfo;
    }
}