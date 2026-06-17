package me.kveex.somelocators.registry;

import com.mojang.serialization.Codec;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.component.PlayerTrackerComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ModComponents {
    public static final DataComponentType<LodestonePointComponent> LODESTONE_POINT_COMPONENT = register(
            "better_tracker_component",
            LodestonePointComponent.CODEC
    );

    public static final DataComponentType<PlayerTrackerComponent> ENTITY_TRACKER_COMPONENT = register(
            "entity_tracker_component",
            PlayerTrackerComponent.CODEC
    );

    private static <T> DataComponentType<T> register(String name, Codec<T> codec) {
        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, name),
                DataComponentType.<T>builder().persistent(codec).build()
        );
    }

    public static void init() {
        SomeLocators.LOGGER.info("Components loaded");
    }
}
