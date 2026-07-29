package me.kveex.somelocators.registry;

import me.kveex.somelocators.component.LocatorComponent;
import me.kveex.somelocators.component.PlayerLocatorComponent;
import me.kveex.somelocators.platform.Services;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

public class ModComponents {
    public static final Supplier<DataComponentType<LocatorComponent>> LOCATOR_COMPONENT = Services.REGISTRY.registerComponent(
            "locator_component",
            LocatorComponent.CODEC,
            LocatorComponent.STREAM_CODEC
    );

    public static final Supplier<DataComponentType<PlayerLocatorComponent>> PLAYER_LOCATOR_COMPONENT = Services.REGISTRY.registerComponent(
            "player_locator_component",
            PlayerLocatorComponent.CODEC
    );

    public static void init() {}
}
