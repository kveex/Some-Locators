package me.kveex.bettercoordination.client.ui;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import me.kveex.bettercoordination.BetterCoordination;
import me.kveex.bettercoordination.component.PointComponent;
import me.kveex.bettercoordination.packet.RenamePoint;
import me.kveex.bettercoordination.registry.ModNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class RenameLocatorPointScreen extends BaseUIModelScreen<FlowLayout> {
    private final PointComponent oldPoint;

    public RenameLocatorPointScreen(@UnknownNullability PointComponent oldPoint) {
        super(FlowLayout.class, DataSource.asset(Identifier.of(BetterCoordination.MOD_ID, "locator_point_settings_screen")));
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
    public boolean shouldPause() {
        return false;
    }

    private void buildUi() {
        FlowLayout flowLayout = this.uiAdapter.rootComponent;

        flowLayout.clearChildren();

        FlowLayout child = flowLayout.child(
                this.model.expandTemplate(
                        FlowLayout.class,
                        "point_settings_template",
                        Map.of(
                                "state", Registries.BLOCK.getId(this.oldPoint.blockState().getBlock()).getPath(),
                                "tooltip_block_text", this.oldPoint.blockState().getBlock().getTranslationKey(),
                                "button_text", "ui.better_coordination.rename_point"
                        )
                )
        );

        TextBoxComponent nameTextBox = child.childById(TextBoxComponent.class, "name_text_box");

        ButtonComponent buttonComponent = child.childById(ButtonComponent.class, "create_button")
                .onPress(button -> {
                    RenamePoint renamePoint = new RenamePoint(nameTextBox.getText(), oldPoint);
                    ModNetworking.MOD_CHANNEL.clientHandle().send(renamePoint);
                    this.close();
                });

        nameTextBox.onChanged().subscribe(s -> buttonComponent.active(!s.isEmpty()));
    }
}
