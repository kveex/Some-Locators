package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.element.TextInputElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.CreateLodestonePointPayload;
import me.kveex.somelocators.network.RenamePointPayload;
import me.kveex.somelocators.network.SetLodestonePointPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class LocatorPointScreen extends LocatorRelatedScreen {
    private final GlobalPos globalPos;
    private final BlockState blockState;
    @Nullable private final LodestoneTracker tracker;

    public LocatorPointScreen(CreateLodestonePointPayload createLodestonePoint) {
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
        // Block
        int blockSize = 128;
        var blockCenter = CoordsPair.create(this.width, this.height, blockSize);
        int blockOffset = 24, blockY = blockCenter.y() - blockOffset;

        BlockElement blockElement = new BlockElement(blockCenter.x(), blockY, blockSize, this.blockState, true);

        this.addRenderableWidget(blockElement);

        // Text Input
        int textInputWidth = 120, textInputHeight = this.font.lineHeight + 12;
        var textInputCenter = CoordsPair.create(this.width, this.height, textInputWidth, textInputHeight);
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

        Button buttonWidget = Button.builder(buttonText, (btn) -> {
            String pointName = nameTextBox.getValue().isBlank() ? blockName.getString() : nameTextBox.getValue();
            if (noTracker) {
                SetLodestonePointPayload setLodestonePoint = new SetLodestonePointPayload(pointName, globalPos, blockState);
                setLodestonePoint.send();
            } else {
                RenamePointPayload renamePoint = new RenamePointPayload(pointName, new PointComponent("", blockState, globalPos, tracker));
                renamePoint.send();
            }
            this.onClose();
        }).bounds(textInputCenter.x(), textInputY + textInputHeight, textInputWidth, textInputHeight).build();

        this.addRenderableWidget(buttonWidget);

        // Label
        Component text = Component.translatable("ui.some_locators.new_point_label");
        int labelOffset = 12, labelY = textInputY - labelOffset;

        LabelElement pointLabelElement = new LabelElement(
                textInputCenter.x(), labelY,
                text
        );

        this.addRenderableWidget(pointLabelElement);
    }
}
