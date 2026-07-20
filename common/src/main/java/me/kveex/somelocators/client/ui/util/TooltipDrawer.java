package me.kveex.somelocators.client.ui.util;

import me.kveex.somelocators.Constants;
import me.kveex.somelocators.client.ui.component.PointInfoComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class TooltipDrawer {
    private static final Identifier TOOLTIP_SPRITE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "locator_point");
    private static PointInfoComponent hoveredWidget = null;

    public static void renderTooltipBackground(@NonNull GuiGraphics graphics, int x, int y, int width, int height) {
        TooltipRenderUtil.renderTooltipBackground(graphics, x, y, width, height, TOOLTIP_SPRITE);
    }

    public static void setHoveredWidget(PointInfoComponent pointInfo) {
        hoveredWidget = pointInfo;
    }

    public static boolean isHoveredWidget(PointInfoComponent pointInfo) {
        return hoveredWidget == pointInfo;
    }

    public static void clearHoveredWidget() {
        hoveredWidget = null;
    }

    public static boolean canClaim(PointInfoComponent pointInfo) {
        return hoveredWidget == null || hoveredWidget == pointInfo;
    }

    public static boolean hasHoveredWidget() {
        return hoveredWidget != null;
    }
}