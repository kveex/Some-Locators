package me.kveex.bettercoordination.client.ui;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import me.kveex.bettercoordination.BetterCoordination;
import me.kveex.bettercoordination.component.PointComponent;
import me.kveex.bettercoordination.packet.ChangeTargetPoint;
import me.kveex.bettercoordination.packet.RemovePoint;
import me.kveex.bettercoordination.registry.ModNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class LocatorMenuScreen extends BaseUIModelScreen<FlowLayout> {
    private PointComponent currentPoint;
    private final List<PointComponent> points;
    public static final String xml = "locator_points_screen";
    public LocatorMenuScreen(PointComponent currentPoint, List<PointComponent> points) {
        super(FlowLayout.class, DataSource.asset(Identifier.of(BetterCoordination.MOD_ID, xml)));
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
    public boolean shouldPause() {
        return false;
    }

    private void buildUi() {
        FlowLayout points_layout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "points_layout");

        points_layout.clearChildren();

        for (PointComponent point : points) {
            FlowLayout child = points_layout.child(
                    this.model.expandTemplate(
                            FlowLayout.class,
                            "template_point_info",
                            Map.of(
                                    "state", Registries.BLOCK.getId(point.blockState().getBlock()).getPath(),
                                    "tooltip_text", point.blockState().getBlock().getTranslationKey(),
                                    "point_name", point.name()
                            )
                    )
            );

            child.childById(ButtonComponent.class, "rename_point_button").onPress(buttonComponent -> this.client.setScreen(new RenameLocatorPointScreen(point)));
            child.childById(ButtonComponent.class, "remove_point_button").onPress(buttonComponent -> {
                ModNetworking.MOD_CHANNEL.clientHandle().send(new RemovePoint(point));

                points.removeIf(p -> p.target().equals(point.target()));

                if (currentPoint != null && currentPoint.target().equals(point.target())) {
                    currentPoint = points.isEmpty() ? null : points.getFirst();
                }

                this.buildUi();
            });

            boolean isCurrent = !point.equals(this.currentPoint);
            child.childById(ButtonComponent.class, "track_point_button").onPress(buttonComponent -> {
                ModNetworking.MOD_CHANNEL.clientHandle().send(new ChangeTargetPoint(point));
                this.currentPoint = point;
                this.buildUi();
            }).active(isCurrent);
        }
    }
}
