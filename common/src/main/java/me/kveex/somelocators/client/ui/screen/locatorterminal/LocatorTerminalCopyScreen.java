package me.kveex.somelocators.client.ui.screen.locatorterminal;

import commonnetwork.api.Dispatcher;
import me.kveex.somelocators.client.ui.component.SelectablePointListComponent;
import me.kveex.somelocators.client.ui.element.ButtonElement;
import me.kveex.somelocators.client.ui.element.ItemElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.LocatorComponent;
import me.kveex.somelocators.network.locatorterminal.OpenCopyScreenPayload;
import me.kveex.somelocators.network.locatorterminal.WriteLodestoneComponentPayload;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class LocatorTerminalCopyScreen extends LocatorTerminalRelatedScreen {
    private final ItemStack locator;
    private final ItemStack punchCard;
    private ButtonElement copyButton;
    private ButtonElement deselectAllButton;
    private ButtonElement selectAllButton;
    private boolean buttonDisabled = false;

    public LocatorTerminalCopyScreen(OpenCopyScreenPayload payload) {
        super(payload.pos());
        this.locator = payload.locator();
        this.punchCard = payload.punchCard();
    }

    @Override
    protected void init() {
        super.init();
        if (this.punchCard.has(ModComponents.LOCATOR_COMPONENT.get())) {
            initUI(this.punchCard, this.locator, true);
        } else  {
            initUI(this.locator, this.punchCard, false);
        }
    }

    private void initUI(ItemStack stackFrom, ItemStack stackTo, boolean showRewriteWarning) {
        CoordsPair uiStartCoords = getUiStartCoords();
        int margin = 8, buttonMargin = 2;
        int itemScale = 3, itemSize = 16 * itemScale;
        int buttonPressTicks = showRewriteWarning ? 40 : 0;

        int itemY = uiStartCoords.y() + margin, screenWidth = uiWidth / 2;

        int itemFromCentered = CoordsPair.centeredX(uiStartCoords.x(), screenWidth, itemSize);
        int itemToCentered = CoordsPair.centeredX(uiStartCoords.x() + screenWidth, screenWidth, itemSize);

        ItemElement fromItemElement = new ItemElement(itemFromCentered, itemY, itemScale, stackFrom);
        ItemElement toItemElement = new ItemElement(itemToCentered, itemY, itemScale, stackTo);

        this.addRenderableWidget(fromItemElement);
        this.addRenderableWidget(toItemElement);

        int labelWidth = (uiWidth - margin * 2) / 2;

        LabelElement itemFromName = LabelElement.builder(uiStartCoords.x() + margin, fromItemElement.getY() + itemSize, stackFrom.getHoverName())
                .centered()
                .width(labelWidth)
                .build();

        LabelElement itemToName = LabelElement.builder(uiStartCoords.x() + uiWidth / 2 + margin / 2, fromItemElement.getY() + itemSize, stackTo.getHoverName())
                .centered()
                .width(labelWidth)
                .build();

        this.addRenderableWidget(itemFromName);
        this.addRenderableWidget(itemToName);

        int listY = itemFromName.getY() + margin * 2;
        LocatorComponent component = stackFrom.get(ModComponents.LOCATOR_COMPONENT.get());
        if (component == null) return;

        SelectablePointListComponent list = new SelectablePointListComponent(
                uiStartCoords.x() + margin,
                listY,
                uiWidth - margin * 2,
                96,
                component.points(),
                l -> {
                    this.copyButton.active(!this.buttonDisabled && !l.getSelectedPoints().isEmpty());
                    this.deselectAllButton.active(!this.buttonDisabled);
                    this.selectAllButton.active(!this.buttonDisabled);
                }
        );
        this.addRenderableWidget(list);

        int buttonY = list.getY() + list.getHeight() + margin / 2;
        int buttonWidth = 80;

        int deselectAllButtonX = uiStartCoords.x() + buttonMargin * 3;
        this.deselectAllButton = ButtonElement.builder(Component.translatable("ui.some_locators.locator_terminal_deselect_all_button"), button -> list.moveAllToSelectable())
                .width(buttonWidth)
                .pos(deselectAllButtonX, buttonY)
                .build();

        Component copButtonText = showRewriteWarning
                ? Component.translatable("ui.some_locators.locator_terminal_overwrite_button")
                : Component.translatable("ui.some_locators.locator_terminal_copy_button");
        int copyButtonX = deselectAllButtonX + buttonWidth + buttonMargin;
        this.copyButton = ButtonElement.builder(copButtonText, button -> {
            LocalPlayer player = this.minecraft.player;
            var selectedPoints = list.getSelectedPoints();
            stackTo.set(ModComponents.LOCATOR_COMPONENT.get(), new LocatorComponent(selectedPoints.getFirst(), selectedPoints, false));
            if (stackFrom.getCustomName() != null) {
                stackTo.set(DataComponents.CUSTOM_NAME, stackFrom.getCustomName());
            }
            WriteLodestoneComponentPayload payload = new WriteLodestoneComponentPayload(stackTo, this.getBlockPos());
            Dispatcher.sendToServer(payload);
            if (player != null) {
                player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            }
            list.active = false;
            this.buttonDisabled = true;
            button.active(false);
            this.deselectAllButton.active(false);
            this.selectAllButton.active(false);
        }).pos(copyButtonX, buttonY).width(buttonWidth).ticksAmountForPress(buttonPressTicks, 0xFFEFEFEF).build();
        this.copyButton.active(false);

        int selectAllButtonX = copyButtonX + buttonWidth + buttonMargin;
        this.selectAllButton = ButtonElement.builder(Component.translatable("ui.some_locators.locator_terminal_select_all_button"), button -> list.moveAllToSelected())
                .width(buttonWidth)
                .pos(selectAllButtonX, buttonY)
                .build();

        this.addRenderableWidget(deselectAllButton);
        this.addRenderableWidget(copyButton);
        this.addRenderableWidget(selectAllButton);

        if (showRewriteWarning) {
            int overwriteWarningY = this.height - margin - this.font.lineHeight;
            LabelElement overwriteWarningLabel = LabelElement.builder(margin, overwriteWarningY, Component.translatable("ui.some_locators.locator_terminal_overwrite_warning"))
                    .width(this.width - margin)
                    .centered()
                    .build();

            this.addRenderableWidget(overwriteWarningLabel);
        }

    }
}
