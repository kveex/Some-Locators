package me.kveex.somelocators.client.ui.screen.locatorinspector;

import com.mojang.authlib.GameProfile;
import me.kveex.somelocators.client.ui.component.ExtendedPointInfoListComponent;
import me.kveex.somelocators.client.ui.element.EntityElement;
import me.kveex.somelocators.client.ui.element.ItemElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.component.PlayerTrackerComponent;
import me.kveex.somelocators.config.SomeLocatorsConfig;
import me.kveex.somelocators.network.locatorinspector.OpenInspectScreenPayload;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import static me.kveex.somelocators.client.ui.screen.locator.LocatorMenuScreen.MAX_POINTS_ON_PAGE;

public class LocatorInspectorInspectScreen extends LocatorInspectorRelatedScreen {
    private final ItemStack inspectedStack;
    private final GameProfile gameProfile;

    public LocatorInspectorInspectScreen(OpenInspectScreenPayload payload) {
        super(payload.pos());
        this.inspectedStack = payload.inspectedStack();
        this.gameProfile = payload.playerProfile().isPresent() ? payload.playerProfile().get() : null;
    }

    @Override
    protected void init() {
        super.init();
        CoordsPair uiStartCoords = this.getUiStartCoords();
        int borderMargin = 4;
        int elementMargin = borderMargin * 3;
        LodestonePointComponent lodestoneComponent = this.inspectedStack.get(ModComponents.LODESTONE_POINT_COMPONENT);
        PlayerTrackerComponent playerComponent = this.inspectedStack.get(ModComponents.PLAYER_TRACKER_COMPONENT);

        if (lodestoneComponent != null) {
            int pagesAmount = (int) Math.ceil((double) lodestoneComponent.points().size() / MAX_POINTS_ON_PAGE);

            // Item
            int itemScale = 3, itemSize = 16 * itemScale;
            int itemX = uiStartCoords.x() + borderMargin, itemY = uiStartCoords.y() + borderMargin;
            ItemElement item = new ItemElement(itemX, itemY, itemScale, this.inspectedStack);

            this.addRenderableWidget(item);

            // Name Label
            Component nameText = this.inspectedStack.getHoverName();
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
                    lodestoneComponent.points().size(),
                    pagesAmount
            ).withStyle(ChatFormatting.DARK_GRAY);

            int infoLabelY = nameLabelY + elementMargin;
            LabelElement infoLabel = LabelElement.builder(nameLabelX, infoLabelY, infoText)
                    .width(maxLabelWidth)
                    .build();

            this.addRenderableWidget(infoLabel);

            // Current Point Label
            if (lodestoneComponent.currentPoint().isPresent()) {
                Component currentText = Component.translatable(
                        "ui.some_locators.locator_inspector_current_name"
                ).withStyle(ChatFormatting.DARK_GRAY);

                Component currentName = Component.literal(
                        lodestoneComponent.currentPoint().get().name()
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
                    lodestoneComponent.points()
            );

            this.addRenderableWidget(list);
        } else if (playerComponent != null) {
            if (this.gameProfile == null) {
                initError(Component.translatable("ui.some_locators.locator_inspector_player_not_found"));
                return;
            }

            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            AbstractClientPlayer fakePlayer = new AbstractClientPlayer(level, this.gameProfile) {
                @Override
                public boolean isModelPartShown(@NonNull PlayerModelPart part) {
                    return true;
                }
            };

            int entityHeight = 80, entityWidth = 80, entityY = uiStartCoords.y() + uiHeight / 2 - entityHeight / 2;
            EntityElement playerModel = EntityElement.builder(uiStartCoords.x() + elementMargin, entityY, fakePlayer)
                    .scale(90)
                    .size(entityWidth, entityHeight)
                    .lookAngle(90f, 0f)
                    .yOffset(0.6f)
                    .build();
            this.addRenderableWidget(playerModel);

            int labelWidth = uiWidth - entityWidth - elementMargin * 3;
            int labelX = playerModel.getX() + entityWidth + elementMargin;
            LabelElement playerUUID = LabelElement.builder(
                            labelX, entityY, Component.literal(playerComponent.trackedPlayer()).withStyle(ChatFormatting.DARK_GRAY)
                    )
                    .staticText(Component.literal("UUID:"))
                    .width(labelWidth)
                    .build();

            int playerNameY = uiStartCoords.y() + uiHeight / 2 - font.lineHeight / 2;
            LabelElement playerName = LabelElement.builder(
                    labelX, playerNameY, fakePlayer.getName()
                    )
                    .staticText(Component.translatable("ui.some_locators.locator_inspector_player_name"))
                    .width(labelWidth)
                    .build();


            int timeTrackerAmountY = entityY + entityHeight - font.lineHeight;

            int fullSeconds = SomeLocatorsConfig.playerLocatorTrackingTime;
            int hours = fullSeconds / 3600;
            int minutes = (fullSeconds % 3600) / 60;
            int seconds = fullSeconds % 60;

            LabelElement timeTrackerAmount = LabelElement.builder(
                    labelX, timeTrackerAmountY,
                    Component.translatable("ui.some_locators.locator_name.locator_inspector_tracker_lasts_amount", hours, minutes, seconds)
            ).build();

            this.addRenderableWidget(playerName);
            this.addRenderableWidget(playerUUID);
            this.addRenderableWidget(timeTrackerAmount);
        } else {
            initError(Component.translatable("ui.some_locators.locator_inspector_inspect_error"));
        }
    }

    private void initError(Component errorComponent) {
        CoordsPair uiStartCoords = this.getUiStartCoords();
        int borderMargin = 4;

        LabelElement errorLabel = LabelElement.builder(uiStartCoords.x() + borderMargin + 1, uiWidth / 2 - font.lineHeight, errorComponent)
                .width(uiWidth - 10)
                .centered()
                .build();

        this.addRenderableWidget(errorLabel);
    }
}
