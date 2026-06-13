package me.kveex.somelocators;

import me.kveex.somelocators.config.ModConfig;
import me.kveex.somelocators.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.component.DataComponentTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SomeLocators implements ModInitializer {
    public static final String MOD_ID = "some_locators";
    public static final Logger LOGGER = LoggerFactory.getLogger(SomeLocators.class);
    public static final ModConfig CONFIG = ModConfig.createAndLoad();

    @Override
    public void onInitialize() {
        ModItems.init();
        ModComponents.init();
        ModEvents.init();
        ModNetworking.init();
        ModTags.init();
        ModGroups.init();
        ModVillagerTrades.init();

        ComponentTooltipAppenderRegistry.addAfter(DataComponentTypes.DAMAGE, ModComponents.LODESTONE_POINT_COMPONENT);
        ComponentTooltipAppenderRegistry.addAfter(DataComponentTypes.DAMAGE, ModComponents.ENTITY_TRACKER_COMPONENT);
    }
}
