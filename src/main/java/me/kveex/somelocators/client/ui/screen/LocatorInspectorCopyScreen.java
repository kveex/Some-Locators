package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.client.ui.component.SelectablePointListComponent;
import me.kveex.somelocators.client.ui.element.ItemElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.OpenLocatorInspectorMenuPayload;
import me.kveex.somelocators.registry.ModComponents;
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

        int itemY = uiStartCoords.y() + borderMargin * 2, screenWidth = uiWidth / 2;

        CoordsPair locatorCentered = CoordsPair.createCentered(uiStartCoords, screenWidth, itemY, itemSize, itemSize);
        CoordsPair punchCardCentered = CoordsPair.createCentered(uiStartCoords.x() + screenWidth, 0, screenWidth, itemY, itemSize, itemSize);

        ItemElement locatorItem = new ItemElement(locatorCentered.x(), itemY, itemScale, this.locator);
        ItemElement punchCardItem = new ItemElement(punchCardCentered.x(), itemY, itemScale, this.punchCard);

        this.addRenderableWidget(locatorItem);
        this.addRenderableWidget(punchCardItem);

        int listY = locatorItem.getY() + itemSize + borderMargin;
        LodestonePointComponent component = this.locator.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null) return;

        SelectablePointListComponent list = new SelectablePointListComponent(uiStartCoords.x() + borderMargin, listY, uiWidth - borderMargin * 2, 128, component.points());
        this.addRenderableWidget(list);

    }
}
