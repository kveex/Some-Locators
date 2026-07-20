package me.kveex.somelocators.registry;

import me.kveex.somelocators.component.LocatorComponent;
import me.kveex.somelocators.component.PlayerLocatorComponent;
import me.kveex.somelocators.platform.Services;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

public class ModComponents {
    public static final Supplier<DataComponentType<LocatorComponent>> LODESTONE_POINT_COMPONENT = Services.REGISTRY.registerComponent(
            "better_tracker_component",
            LocatorComponent.CODEC,
            LocatorComponent.STREAM_CODEC
    );

    public static final Supplier<DataComponentType<PlayerLocatorComponent>> PLAYER_TRACKER_COMPONENT = Services.REGISTRY.registerComponent(
            "player_tracker_component",
            PlayerLocatorComponent.CODEC
    );

    public static void init() {}
}
