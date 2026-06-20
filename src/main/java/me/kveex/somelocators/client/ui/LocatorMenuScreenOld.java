package me.kveex.somelocators.client.ui;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.ChangeTargetPoint;
import me.kveex.somelocators.network.RemovePoint;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public class LocatorMenuScreenOld extends BaseUIModelScreen<FlowLayout> {
    private PointComponent currentPoint;
    private final List<PointComponent> points;
    public static final String xml = "locator_points_screen";
    public LocatorMenuScreenOld(PointComponent currentPoint, List<PointComponent> points) {
        super(FlowLayout.class, DataSource.asset(Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, xml)));
        this.currentPoint = currentPoint;
        this.points = points;
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
        FlowLayout points_layout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "points_layout");

        points_layout.clearChildren();

        for (PointComponent point : points) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(point.blockState().getBlock());
            FlowLayout child = points_layout.child(
                    this.model.expandTemplate(
                            FlowLayout.class,
                            "template_point_info",
                            Map.of(
                                    "state", id.toString(),
                                    "tooltip_text", point.blockState().getBlock().getDescriptionId(),
                                    "point_name", point.name()
                            )
                    )
            );

            child.childById(ButtonComponent.class, "rename_point_button").onPress(buttonComponent -> this.minecraft.setScreen(new RenameLocatorPointScreenOld(point)));
            child.childById(ButtonComponent.class, "remove_point_button").onPress(buttonComponent -> {
                RemovePoint removePoint = new RemovePoint(point);
                removePoint.send();
                points.removeIf(p -> p.target().equals(point.target()));

                if (currentPoint != null && currentPoint.target().equals(point.target())) {
                    currentPoint = points.isEmpty() ? null : points.getFirst();
                }

                this.buildUi();
            });

            boolean isCurrent = !point.equals(this.currentPoint);
            child.childById(ButtonComponent.class, "track_point_button").onPress(buttonComponent -> {
                ChangeTargetPoint changeTargetPoint = new ChangeTargetPoint(point);
                changeTargetPoint.send();
                this.currentPoint = point;
                this.buildUi();
            }).active(isCurrent);
        }
    }
}
