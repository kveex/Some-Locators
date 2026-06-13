package me.kveex.somelocators.registry;

import com.mojang.serialization.Codec;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.component.PlayerTrackerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {
    public static final ComponentType<LodestonePointComponent> LODESTONE_POINT_COMPONENT = register(
            "better_tracker_component",
            LodestonePointComponent.CODEC
    );

    public static final ComponentType<PlayerTrackerComponent> ENTITY_TRACKER_COMPONENT = register(
            "entity_tracker_component",
            PlayerTrackerComponent.CODEC
    );

    private static <T> ComponentType<T> register(String name, Codec<T> codec) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(SomeLocators.MOD_ID, name),
                ComponentType.<T>builder().codec(codec).build()
        );
    }

    public static void init() {
        SomeLocators.LOGGER.info("Components loaded");
    }
}
