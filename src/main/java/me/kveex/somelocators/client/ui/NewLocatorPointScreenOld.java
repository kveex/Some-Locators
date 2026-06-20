package me.kveex.somelocators.client.ui;


import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.network.CreateLodestonePoint;
import me.kveex.somelocators.network.SetLodestonePoint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.core.GlobalPos;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class NewLocatorPointScreenOld extends BaseUIModelScreen<FlowLayout> {
    private final GlobalPos globalPos;
    private final BlockState blockState;

    public NewLocatorPointScreenOld(CreateLodestonePoint createLodestonePoint) {
        super(FlowLayout.class, DataSource.asset(Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "locator_point_settings_screen")));
        this.globalPos = createLodestonePoint.globalPos();
        this.blockState = createLodestonePoint.blockState();
    }

    @Override
    protected void build(FlowLayout rootComponent) {

    }

    @Override
    protected void init() {
        super.init();
        buildUi();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void buildUi() {
        FlowLayout flowLayout = this.uiAdapter.rootComponent;

        flowLayout.clearChildren();
        Identifier id = BuiltInRegistries.BLOCK.getKey(this.blockState.getBlock());

        FlowLayout child = flowLayout.child(
                this.model.expandTemplate(
                        FlowLayout.class,
                        "point_settings_template",
                        Map.of(
                                "state", id.toString(),
                                "tooltip_block_text", this.blockState.getBlock().getDescriptionId(),
                                "button_text", "ui.some_locators.create_point"
                        )
                )
        );

        TextBoxComponent nameTextBox = child.childById(TextBoxComponent.class, "name_text_box");

        ButtonComponent buttonComponent = child.childById(ButtonComponent.class, "create_button")
                .onPress(button -> {
                    SetLodestonePoint setLodestonePoint = new SetLodestonePoint(nameTextBox.getValue(), globalPos, blockState);
                    setLodestonePoint.send();
                    this.onClose();
                });

        nameTextBox.onChanged().subscribe(s -> buttonComponent.active(!s.isEmpty()));
    }
}
