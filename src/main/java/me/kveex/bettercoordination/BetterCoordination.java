package me.kveex.bettercoordination;

import me.kveex.bettercoordination.config.ModConfig;
import me.kveex.bettercoordination.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.component.DataComponentTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterCoordination implements ModInitializer {
    public static final String MOD_ID = "better_coordination";
    public static final Logger LOGGER = LoggerFactory.getLogger(BetterCoordination.class);
    public static final ModConfig CONFIG = ModConfig.createAndLoad();

    @Override
    public void onInitialize() {
        ModItems.init();
        ModComponents.init();
        ModEvents.init();
        ModNetworking.init();
        ModTags.init();

        ComponentTooltipAppenderRegistry.addAfter(DataComponentTypes.DAMAGE, ModComponents.LODESTONE_POINT_COMPONENT);
        ComponentTooltipAppenderRegistry.addAfter(DataComponentTypes.DAMAGE, ModComponents.ENTITY_TRACKER_COMPONENT);
    }
}
