package me.kveex.bettercoordination.registry;

import com.mojang.serialization.Codec;
import me.kveex.bettercoordination.BetterCoordination;
import me.kveex.bettercoordination.component.BetterLodestoneTrackerComponent;
import me.kveex.bettercoordination.component.EntityTracker;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {
    public static final ComponentType<BetterLodestoneTrackerComponent> BETTER_TRACKER_COMPONENT = register(
            "better_tracker_component",
            BetterLodestoneTrackerComponent.CODEC
    );

    public static final ComponentType<EntityTracker> ENTITY_TRACKER_COMPONENT = register(
            "entity_tracker_component",
            EntityTracker.CODEC
    );

    private static <T> ComponentType<T> register(String name, Codec<T> codec) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(BetterCoordination.MOD_ID, name),
                ComponentType.<T>builder().codec(codec).build()
        );
    }

    public static void init() {
        BetterCoordination.LOGGER.info("Components loaded");
    }
}
