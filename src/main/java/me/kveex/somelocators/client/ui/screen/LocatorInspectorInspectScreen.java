package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.client.ui.component.ExtendedPointInfoListComponent;
import me.kveex.somelocators.client.ui.element.ItemElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.OpenLocatorInspectorMenuPayload;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static me.kveex.somelocators.client.ui.screen.LocatorMenuScreen.MAX_POINTS_ON_PAGE;

public class LocatorInspectorInspectScreen extends LocatorInspectorRelatedScreen {
    private final ItemStack locator;
    private final ItemStack punchCard;

    public LocatorInspectorInspectScreen(OpenLocatorInspectorMenuPayload payload) {
        super(payload.pos());
        locator = payload.locator();
        punchCard = payload.punchCard();
    }

    @Override
    protected void init() {
        super.init();
        ItemStack inspected = locator.isEmpty() ? punchCard : locator;
        initInspectScreen(inspected);
    }

    private void initInspectScreen(ItemStack stack) {
        CoordsPair uiStartCoords = this.getUiStartCoords();
        int uiWidth = this.getUiWidth();
        int borderMargin = 4;
        LodestonePointComponent component = stack.get(ModComponents.LODESTONE_POINT_COMPONENT);

        if (component == null) {
            Component text = Component.translatable("ui.some_locators.locator_inspector_inspect_error");
            LabelElement errorLabel = LabelElement.builder(uiStartCoords.x() + borderMargin + 1, uiWidth / 2 - font.lineHeight, text)
                    .width(uiWidth - 10)
                    .centered()
                    .build();

            this.addRenderableWidget(errorLabel);
            return;
        }

        int pagesAmount = (int) Math.ceil((double) component.points().size() / MAX_POINTS_ON_PAGE);

        // Item
        int itemScale = 3, itemSize = 16 * itemScale;
        int elementMargin = borderMargin * 3;
        int itemX = uiStartCoords.x() + borderMargin, itemY = uiStartCoords.y() + borderMargin;
        ItemElement item = new ItemElement(itemX, itemY, itemScale, stack);

        this.addRenderableWidget(item);

        // Name Label
        Component nameText = stack.getHoverName();
        int nameLabelX = uiStartCoords.x() + itemSize + elementMargin, nameLabelY = uiStartCoords.y() + elementMargin;
        int maxLabelWidth = uiWidth - itemSize - borderMargin * itemScale - elementMargin;
        LabelElement nameLabel = LabelElement.builder(nameLabelX, nameLabelY, nameText)
                .width(maxLabelWidth)
                .staticText(Component.translatable("ui.some_locators.item_name"))
                .build();

        this.addRenderableWidget(nameLabel);

        // Info Label
        Component infoText = Component.translatable(
                "ui.some_locators.locator_inspector_points_amount",
                component.points().size(),
                pagesAmount
        ).withStyle(ChatFormatting.DARK_GRAY);

        int infoLabelY = nameLabelY + elementMargin;
        LabelElement infoLabel = LabelElement.builder(nameLabelX, infoLabelY, infoText)
                .width(maxLabelWidth)
                .build();

        this.addRenderableWidget(infoLabel);

        // Current Point Label
        if (component.currentPoint().isPresent()) {
            Component currentText = Component.translatable(
                    "ui.some_locators.locator_inspector_current_name"
            ).withStyle(ChatFormatting.DARK_GRAY);

            Component currentName = Component.literal(
                    component.currentPoint().get().name()
            ).withStyle(ChatFormatting.DARK_GRAY);

            int currentLabelY = infoLabelY + elementMargin;
            LabelElement currentLabel = LabelElement.builder(nameLabelX, currentLabelY, currentName)
                    .width(maxLabelWidth)
                    .staticText(currentText)
                    .build();

            this.addRenderableWidget(currentLabel);
        }

        int listX = uiStartCoords.x() + borderMargin * 2, listY = uiStartCoords.y() + itemSize;
        int listWidth = uiWidth - borderMargin * 4, listHeight = 132 - borderMargin * 2 + elementMargin;

        ExtendedPointInfoListComponent list = new ExtendedPointInfoListComponent(
                listX, listY,
                listWidth, listHeight,
                component.points()
        );

        this.addRenderableWidget(list);
    }
}
