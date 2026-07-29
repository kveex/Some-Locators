package me.kveex.somelocators.client.ui.screen.locator;

import commonnetwork.api.Dispatcher;
import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.ButtonElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.element.TextInputElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.locator.CreateLocatorPointPayload;
import me.kveex.somelocators.network.locator.RenamePointPayload;
import me.kveex.somelocators.network.locator.SetLocatorPointPayload;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class LocatorPointScreen extends LocatorRelatedScreen {
    private final GlobalPos globalPos;
    private final BlockState blockState;
    @Nullable private final LodestoneTracker tracker;

    public LocatorPointScreen(CreateLocatorPointPayload createLodestonePoint) {
        this.blockState = createLodestonePoint.blockState();
        this.globalPos = createLodestonePoint.globalPos();
        this.tracker = null;
    }

    public LocatorPointScreen(PointComponent oldPoint) {
        this.blockState = oldPoint.blockState();
        this.globalPos = oldPoint.target();
        this.tracker = oldPoint.lodestoneTracker();
    }

    @Override
    protected void init() {
        super.init();
        // Block
        int blockSize = 128;
        int blockOffset = 24;
        var blockCenter = CoordsPair.centered(0, 0, this.width, this.height, blockSize, blockSize);

        BlockElement blockElement = new BlockElement(blockCenter.x(), blockCenter.y() - blockOffset, blockSize, this.blockState, true);

        this.addRenderableWidget(blockElement);

        // Text Input
        int textInputWidth = 120, textInputHeight = this.font.lineHeight + 12, elementMargin = 4;
        var textInputCenter = CoordsPair.centered(0, 0, this.width, this.height, textInputWidth, textInputHeight);
        int textInputY = textInputCenter.y() + 80;

        Component blockName = Component.translatable(this.blockState.getBlock().getDescriptionId());
        TextInputElement nameTextBox = new TextInputElement(
                textInputCenter.x(), textInputY,
                textInputWidth, textInputHeight,
                blockName
        );

        this.addRenderableWidget(nameTextBox);

        // Button
        boolean noTracker = this.tracker == null;
        Component buttonText = noTracker ?
                Component.translatable("ui.some_locators.create_point") :
                Component.translatable("ui.some_locators.rename_point");

        ButtonElement buttonWidget = ButtonElement.builder(buttonText, button -> {
            String pointName = nameTextBox.getValue().isBlank() ? blockName.getString() : nameTextBox.getValue();
            var payload = noTracker
                    ? new SetLocatorPointPayload(pointName, globalPos, blockState)
                    : new RenamePointPayload(pointName, new PointComponent("", blockState, globalPos, tracker));
            Dispatcher.sendToServer(payload);
            this.onClose();
        }).pos(textInputCenter.x(), textInputY + textInputHeight + elementMargin).width(textInputWidth).build();

        this.addRenderableWidget(buttonWidget);

        // Label
        Component text = Component.translatable("ui.some_locators.point_name");
        int labelOffset = 12, labelY = textInputY - labelOffset;

        LabelElement pointLabelElement = LabelElement.builder(textInputCenter.x(), labelY, text).build();

        this.addRenderableWidget(pointLabelElement);
    }
}
