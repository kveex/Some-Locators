package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.client.ui.component.SelectablePointListComponent;
import me.kveex.somelocators.client.ui.element.ButtonElement;
import me.kveex.somelocators.client.ui.element.ItemElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.OpenLocatorInspectorMenuPayload;
import me.kveex.somelocators.network.WriteLodestoneComponentPayload;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class LocatorInspectorCopyScreen extends LocatorInspectorRelatedScreen {
    private final ItemStack locator;
    private final ItemStack punchCard;
    private ButtonElement copyButton;
    private ButtonElement deselectAllButton;
    private ButtonElement selectAllButton;
    private boolean buttonDisabled = false;

    public LocatorInspectorCopyScreen(OpenLocatorInspectorMenuPayload payload) {
        super(payload.pos());
        this.locator = payload.locator();
        this.punchCard = payload.punchCard();
    }

    @Override
    protected void init() {
        super.init();
        if (this.punchCard.has(ModComponents.LODESTONE_POINT_COMPONENT)) {
            initUI(this.punchCard, this.locator, true);
        } else  {
            initUI(this.locator, this.punchCard, false);
        }
    }

    private void initUI(ItemStack stackFrom, ItemStack stackTo, boolean showRewriteWarning) {
        CoordsPair uiStartCoords = getUiStartCoords();
        int uiWidth = getUiWidth();
        int margin = 8;
        int itemScale = 3, itemSize = 16 * itemScale;
        int buttonPressTicks = showRewriteWarning ? 60 : 0;

        int itemY = uiStartCoords.y() + margin, screenWidth = uiWidth / 2;

        CoordsPair itemFromCentered = CoordsPair.createCentered(uiStartCoords, screenWidth, itemY, itemSize, itemSize);
        CoordsPair itemToCentered = CoordsPair.createCentered(uiStartCoords.x() + screenWidth, 0, screenWidth, itemY, itemSize, itemSize);

        ItemElement fromItemElement = new ItemElement(itemFromCentered.x(), itemY, itemScale, stackFrom);
        ItemElement toItemElement = new ItemElement(itemToCentered.x(), itemY, itemScale, stackTo);

        this.addRenderableWidget(fromItemElement);
        this.addRenderableWidget(toItemElement);

        int labelWidth = (uiWidth - margin * 2) / 2;

        LabelElement itemFromName = LabelElement.builder(uiStartCoords.x() + margin, fromItemElement.getY() + itemSize, stackFrom.getHoverName())
                .centered()
                .width(labelWidth)
                .build();

        LabelElement itemToName = LabelElement.builder(uiStartCoords.x() + uiWidth / 2 + margin, fromItemElement.getY() + itemSize, stackTo.getHoverName())
                .centered()
                .width(labelWidth)
                .build();

        this.addRenderableWidget(itemFromName);
        this.addRenderableWidget(itemToName);

        int listY = itemFromName.getY() + margin * 2;
        LodestonePointComponent component = stackFrom.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null) return;

        SelectablePointListComponent list = new SelectablePointListComponent(
                uiStartCoords.x() + margin,
                listY,
                uiWidth - margin * 2,
                80,
                component.points(),
                l -> {
                    this.copyButton.active(!this.buttonDisabled && !l.getSelectedPoints().isEmpty());
                    this.deselectAllButton.active(!this.buttonDisabled);
                    this.selectAllButton.active(!this.buttonDisabled);
                }
        );
        this.addRenderableWidget(list);

        int buttonY = list.getY() + list.getHeight() + margin;
        int buttonWidth = 75;
        this.copyButton = ButtonElement.builder(Component.literal("Copy"), button -> {
            var selectedPoints = list.getSelectedPoints();
            stackTo.set(ModComponents.LODESTONE_POINT_COMPONENT, new LodestonePointComponent(selectedPoints.getFirst(), selectedPoints, false));
            WriteLodestoneComponentPayload payload = new WriteLodestoneComponentPayload(stackTo, this.getBlockPos());
            payload.send();
            list.active = false;
            this.buttonDisabled = true;
            button.active(false);
            this.deselectAllButton.active(false);
            this.selectAllButton.active(false);
        }).pos(uiStartCoords.x() + screenWidth - buttonWidth / 2, buttonY).width(buttonWidth).ticksAmountForPress(buttonPressTicks).build();
        this.copyButton.active(false);

        int deselectAllButtonX = uiStartCoords.x() + margin;
        this.deselectAllButton = ButtonElement.builder(Component.literal("Unselect All"), button -> list.moveAllToSelectable())
                .width(buttonWidth)
                .pos(deselectAllButtonX, buttonY)
                .build();

        int selectAllButtonX = deselectAllButton.getX() + (deselectAllButton.getWidth() + margin) * 2;
        this.selectAllButton = ButtonElement.builder(Component.literal("Select All"), button -> list.moveAllToSelected())
                .width(buttonWidth)
                .pos(selectAllButtonX, buttonY)
                .build();

        this.addRenderableWidget(deselectAllButton);
        this.addRenderableWidget(copyButton);
        this.addRenderableWidget(selectAllButton);
    }
}
