package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.component.SelectablePointListComponent;
import me.kveex.somelocators.client.ui.element.ButtonElement;
import me.kveex.somelocators.client.ui.element.ItemElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.OpenLocatorInspectorMenuPayload;
import me.kveex.somelocators.network.WritePunchCardPayload;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class LocatorInspectorCopyScreen extends LocatorInspectorRelatedScreen {
    private final ItemStack locator;
    private final ItemStack punchCard;
    private ButtonElement copyButton;
    private boolean buttonDisabled = false;

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
        int margin = 8;
        int itemScale = 3, itemSize = 16 * itemScale;

        int itemY = uiStartCoords.y() + margin, screenWidth = uiWidth / 2;

        CoordsPair locatorCentered = CoordsPair.createCentered(uiStartCoords, screenWidth, itemY, itemSize, itemSize);
        CoordsPair punchCardCentered = CoordsPair.createCentered(uiStartCoords.x() + screenWidth, 0, screenWidth, itemY, itemSize, itemSize);

        ItemElement locatorItem = new ItemElement(locatorCentered.x(), itemY, itemScale, this.locator);
        ItemElement punchCardItem = new ItemElement(punchCardCentered.x(), itemY, itemScale, this.punchCard);

        this.addRenderableWidget(locatorItem);
        this.addRenderableWidget(punchCardItem);

        int labelWidth = (uiWidth - margin * 2) / 2;

        LabelElement locatorName = LabelElement.builder(uiStartCoords.x() + margin, locatorItem.getY() + itemSize, this.locator.getHoverName())
                .centered()
                .width(labelWidth)
                .build();

        LabelElement punchCardName = LabelElement.builder(uiStartCoords.x() + uiWidth / 2 + margin, locatorItem.getY() + itemSize, this.punchCard.getHoverName())
                .centered()
                .width(labelWidth)
                .build();

        this.addRenderableWidget(locatorName);
        this.addRenderableWidget(punchCardName);

        int listY = locatorName.getY() + margin * 2;
        LodestonePointComponent component = this.locator.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null) return;

        SelectablePointListComponent list = new SelectablePointListComponent(
                uiStartCoords.x() + margin,
                listY,
                uiWidth - margin * 2,
                80,
                component.points(),
                l -> this.copyButton.active(!this.buttonDisabled && !l.getSelectedPoints().isEmpty())
        );
        this.addRenderableWidget(list);

        int buttonY = list.getY() + list.getHeight() + margin;
        int buttonWidth = 75;
        this.copyButton = ButtonElement.builder(Component.literal("Copy"), button -> {
            var selectedPoints = list.getSelectedPoints();
            SomeLocators.LOGGER.info("Selected points: {}", selectedPoints);
            punchCard.set(ModComponents.LODESTONE_POINT_COMPONENT, new LodestonePointComponent(selectedPoints.getFirst(), selectedPoints, false));
            WritePunchCardPayload payload = new WritePunchCardPayload(punchCard, this.getBlockPos());
            payload.send();
            this.buttonDisabled = true;
            button.active(false);
        }).pos(uiStartCoords.x() + screenWidth - buttonWidth / 2, buttonY).width(buttonWidth).build();
        this.copyButton.active(false);

        int deselectAllButtonX = uiStartCoords.x() + margin;
        ButtonElement deselectAllButton = ButtonElement.builder(Component.literal("Unselect All"), button -> list.moveAllToSelectable())
                .width(buttonWidth)
                .pos(deselectAllButtonX, buttonY)
                .build();

        int selectAllButtonX = deselectAllButton.getX() + (deselectAllButton.getWidth() + margin) * 2;
        ButtonElement selectAllButton = ButtonElement.builder(Component.literal("Select All"), button -> list.moveAllToSelected())
                .width(buttonWidth)
                .pos(selectAllButtonX, buttonY)
                .build();

        this.addRenderableWidget(deselectAllButton);
        this.addRenderableWidget(copyButton);
        this.addRenderableWidget(selectAllButton);
    }
}
