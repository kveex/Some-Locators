package me.kveex.somelocators.client.ui.screen.locatorterminal;

import com.mojang.authlib.GameProfile;
import me.kveex.somelocators.CommonConfig;
import me.kveex.somelocators.client.ui.component.ExtendedPointInfoListComponent;
import me.kveex.somelocators.client.ui.element.EntityElement;
import me.kveex.somelocators.client.ui.element.ItemElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.LocatorComponent;
import me.kveex.somelocators.component.PlayerLocatorComponent;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.locatorterminal.OpenInspectScreenPayload;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static me.kveex.somelocators.client.ui.screen.locator.LocatorMenuScreen.MAX_POINTS_ON_PAGE;

public class LocatorTerminalInspectScreen extends LocatorTerminalRelatedScreen {
    private final ItemStack inspectedStack;

    public LocatorTerminalInspectScreen(OpenInspectScreenPayload payload) {
        super(payload.pos());
        this.inspectedStack = payload.inspectedStack();
    }

    @Override
    protected void init() {
        super.init();
        CoordsPair uiStartCoords = this.getUiStartCoords();
        int borderMargin = 4;
        int elementMargin = borderMargin * 3;
        LocatorComponent lodestoneComponent = this.inspectedStack.get(ModComponents.LOCATOR_COMPONENT.get());
        PlayerLocatorComponent playerComponent = this.inspectedStack.get(ModComponents.PLAYER_LOCATOR_COMPONENT.get());
        LodestoneTracker compassComponent = this.inspectedStack.get(DataComponents.LODESTONE_TRACKER);

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
                    "ui.some_locators.locator_terminal_points_amount",
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
                        "ui.some_locators.locator_terminal_current_name"
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
            int listWidth = uiWidth - borderMargin * 4, listHeight = 132 + elementMargin;

            ExtendedPointInfoListComponent list = new ExtendedPointInfoListComponent(
                    listX, listY,
                    listWidth, listHeight,
                    lodestoneComponent.points(),
                    false
            );

            this.addRenderableWidget(list);
        } else if (playerComponent != null) {
            AbstractClientPlayer fakePlayer = makeFakePlayer(playerComponent.gameProfile());

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
                            labelX, entityY, Component.literal(playerComponent.gameProfile().id().toString()).withStyle(ChatFormatting.DARK_GRAY)
                    )
                    .staticText(Component.literal("UUID:"))
                    .width(labelWidth)
                    .build();

            int playerNameY = uiStartCoords.y() + uiHeight / 2 - font.lineHeight / 2;
            LabelElement playerName = LabelElement.builder(
                    labelX, playerNameY, Component.literal(playerComponent.gameProfile().name())
                    )
                    .staticText(Component.translatable("ui.some_locators.locator_terminal_player_name"))
                    .width(labelWidth)
                    .build();


            int timeTrackerAmountY = entityY + entityHeight - font.lineHeight;

            int fullSeconds = CommonConfig.playerLocatorTrackingTime;
            int hours = fullSeconds / 3600;
            int minutes = (fullSeconds % 3600) / 60;
            int seconds = fullSeconds % 60;

            LabelElement timeTrackerAmount = LabelElement.builder(
                    labelX, timeTrackerAmountY,
                    Component.translatable("ui.some_locators.locator_terminal_tracker_lasts_amount", hours, minutes, seconds)
            ).build();

            this.addRenderableWidget(playerName);
            this.addRenderableWidget(playerUUID);
            this.addRenderableWidget(timeTrackerAmount);
        } else if (compassComponent != null) {
            int itemScale = 3, itemSize = 16 * itemScale;
            int itemX = uiStartCoords.x() + borderMargin, itemY = uiStartCoords.y() + borderMargin;
            ItemElement item = new ItemElement(itemX, itemY, itemScale, this.inspectedStack);

            this.addRenderableWidget(item);

            // Name Label
            Component nameText = this.inspectedStack.getHoverName();
            int nameLabelX = uiStartCoords.x() + itemSize + elementMargin, nameLabelY = item.getY() + itemSize / 2 - this.font.lineHeight / 2;
            int maxLabelWidth = uiWidth - itemSize - borderMargin * itemScale - elementMargin;
            LabelElement nameLabel = LabelElement.builder(nameLabelX, nameLabelY, nameText)
                    .width(maxLabelWidth)
                    .staticText(Component.translatable("ui.some_locators.item_name"))
                    .build();

            this.addRenderableWidget(nameLabel);

            int listX = uiStartCoords.x() + borderMargin * 2, listY = uiStartCoords.y() + itemSize;
            int listWidth = uiWidth - borderMargin * 4, listHeight = 132 + elementMargin;

            BlockState lodestone = Blocks.LODESTONE.defaultBlockState();

            GlobalPos pos = compassComponent.target().isPresent()
                    ? compassComponent.target().get()
                    : GlobalPos.of(Level.OVERWORLD, BlockPos.ZERO);

            PointComponent fakePoint = new PointComponent(lodestone.getBlock().getName().getString(), lodestone, pos);

            ExtendedPointInfoListComponent list = new ExtendedPointInfoListComponent(
                    listX, listY,
                    listWidth, listHeight,
                    List.of(fakePoint),
                    compassComponent.target().isEmpty()
            );

            this.addRenderableWidget(list);
        } else {
            initError(Component.translatable("message.some_locators.locator_terminal_inspect_error"));
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

    private AbstractClientPlayer makeFakePlayer(GameProfile gameProfile) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;

        return new AbstractClientPlayer(level, gameProfile) {
            @Override
            public boolean isModelPartShown(@NonNull PlayerModelPart part) {
                return true;
            }

            @Override
            protected @NonNull PlayerInfo getPlayerInfo() {
                return new PlayerInfo(gameProfile, false);
            }
        };
    }
}
