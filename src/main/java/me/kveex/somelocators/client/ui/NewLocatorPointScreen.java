package me.kveex.somelocators.client.ui;


import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.packet.CreateLodestonePoint;
import me.kveex.somelocators.packet.SetLodestonePoint;
import me.kveex.somelocators.registry.ModNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class NewLocatorPointScreen extends BaseUIModelScreen<FlowLayout> {
    private final GlobalPos globalPos;
    private final BlockState blockState;

    public NewLocatorPointScreen(CreateLodestonePoint createLodestonePoint) {
        super(FlowLayout.class, DataSource.asset(Identifier.of(SomeLocators.MOD_ID, "locator_point_settings_screen")));
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
    public boolean shouldPause() {
        return false;
    }

    private void buildUi() {
        FlowLayout flowLayout = this.uiAdapter.rootComponent;

        flowLayout.clearChildren();
        Identifier id = Registries.BLOCK.getId(this.blockState.getBlock());

        FlowLayout child = flowLayout.child(
                this.model.expandTemplate(
                        FlowLayout.class,
                        "point_settings_template",
                        Map.of(
                                "state", id.toString(),
                                "tooltip_block_text", this.blockState.getBlock().getTranslationKey(),
                                "button_text", "ui.some_locators.create_point"
                        )
                )
        );

        TextBoxComponent nameTextBox = child.childById(TextBoxComponent.class, "name_text_box");

        ButtonComponent buttonComponent = child.childById(ButtonComponent.class, "create_button")
                .onPress(button -> {
                    SetLodestonePoint setLodestonePoint = new SetLodestonePoint(nameTextBox.getText(), globalPos, blockState);
                    ModNetworking.MOD_CHANNEL.clientHandle().send(setLodestonePoint);
                    this.close();
                });

        nameTextBox.onChanged().subscribe(s -> buttonComponent.active(!s.isEmpty()));
    }
}
