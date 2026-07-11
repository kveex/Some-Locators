package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.client.ui.element.ItemElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.network.OpenLocatorInspectorMenuPayload;
import net.minecraft.world.item.ItemStack;

public class LocatorInspectorCopyScreen extends LocatorInspectorRelatedScreen {
    private final ItemStack locator;
    private final ItemStack punchCard;

    public LocatorInspectorCopyScreen(OpenLocatorInspectorMenuPayload payload) {
        super(payload.pos());
        this.locator = payload.locator();
        this.punchCard = payload.punchCard();
    }

    @Override
    protected void init() {
        super.init();
        CoordsPair uiStartCoords = getUiStartCoords();
        int uiWidth = getUiWidth();
        int borderMargin = 4;
        int itemScale = 3, itemSize = 16 * itemScale;

        int y = uiStartCoords.y() + borderMargin * 2, screenWidth = uiWidth / 2;

        CoordsPair locatorCentered = CoordsPair.createCentered(uiStartCoords, screenWidth, y, itemSize, itemSize);
        CoordsPair punchCardCentered = CoordsPair.createCentered(uiStartCoords.x() + screenWidth, 0, screenWidth, y, itemSize, itemSize);

        ItemElement locatorItem = new ItemElement(locatorCentered.x(), y, itemScale, this.locator);
        ItemElement punchCardItem = new ItemElement(punchCardCentered.x(), y, itemScale, this.punchCard);

        this.addRenderableWidget(locatorItem);
        this.addRenderableWidget(punchCardItem);

    }
}
