package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.client.ui.element.BlockElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.element.TextInputElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.network.CreateLodestonePoint;
import me.kveex.somelocators.network.SetLodestonePoint;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class NewLocatorPointScreen extends LocatorRelatedScreen {
    private final GlobalPos globalPos;
    private final BlockState blockState;

    public NewLocatorPointScreen(CreateLodestonePoint createLodestonePoint) {
        this.blockState = createLodestonePoint.blockState();
        this.globalPos = createLodestonePoint.globalPos();
    }

    @Override
    protected void init() {
        // Block
        int blockSize = 128;
        var blockCenter = CoordsPair.create(this.width, this.height, blockSize);
        int blockOffset = 24, blockY = blockCenter.y() - blockOffset;

        BlockElement blockElement = new BlockElement(blockCenter.x(), blockY, blockSize, this.blockState);
        blockElement.setTooltip(Component.translatable(this.blockState.getBlock().getDescriptionId()));

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
        Button buttonWidget = Button.builder(Component.translatable("ui.some_locators.create_point"), (btn) -> {
            String pointName = nameTextBox.getValue().isBlank() ? blockName.getString() : nameTextBox.getValue();
            SetLodestonePoint setLodestonePoint = new SetLodestonePoint(pointName, globalPos, blockState);
            setLodestonePoint.send();
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
