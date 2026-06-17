package me.kveex.somelocators.client.ui;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.RenamePoint;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class RenameLocatorPointScreen extends BaseUIModelScreen<FlowLayout> {
    private final PointComponent oldPoint;

    public RenameLocatorPointScreen(@UnknownNullability PointComponent oldPoint) {
        super(FlowLayout.class, DataSource.asset(Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "locator_point_settings_screen")));
        this.oldPoint = oldPoint;
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
        Identifier id = BuiltInRegistries.BLOCK.getKey(this.oldPoint.blockState().getBlock());

        FlowLayout child = flowLayout.child(
                this.model.expandTemplate(
                        FlowLayout.class,
                        "point_settings_template",
                        Map.of(
                                "state", id.toString(),
                                "tooltip_block_text", this.oldPoint.blockState().getBlock().getDescriptionId(),
                                "button_text", "ui.some_locators.rename_point"
                        )
                )
        );

        TextBoxComponent nameTextBox = child.childById(TextBoxComponent.class, "name_text_box");

        ButtonComponent buttonComponent = child.childById(ButtonComponent.class, "create_button")
                .onPress(button -> {
                    RenamePoint renamePoint = new RenamePoint(nameTextBox.getValue(), oldPoint);
                    renamePoint.send();
                    this.onClose();
                });

        nameTextBox.onChanged().subscribe(s -> buttonComponent.active(!s.isEmpty()));
    }
}
