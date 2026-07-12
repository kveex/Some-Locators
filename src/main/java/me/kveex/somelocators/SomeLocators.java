package me.kveex.somelocators;

import eu.midnightdust.lib.config.MidnightConfig;
import me.kveex.somelocators.config.SomeLocatorsConfig;
import me.kveex.somelocators.network.*;
import me.kveex.somelocators.network.util.Payloads;
import me.kveex.somelocators.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.core.component.DataComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SomeLocators implements ModInitializer {
    public static final String MOD_ID = "some_locators";
    public static final Logger LOGGER = LoggerFactory.getLogger(SomeLocators.class);

    @Override
    public void onInitialize() {
        ModItems.init();
        ModComponents.init();
        ModBlocks.init();
        ModBlockEntities.init();
        ModEvents.init();
        registerPayloadTypes();
        ModNetworking.init();
        ModTags.init();
        ModGroups.init();
        ModVillagerTrades.init();

        ComponentTooltipAppenderRegistry.addAfter(DataComponents.DAMAGE, ModComponents.LODESTONE_POINT_COMPONENT);
        ComponentTooltipAppenderRegistry.addAfter(DataComponents.DAMAGE, ModComponents.ENTITY_TRACKER_COMPONENT);

        MidnightConfig.init(MOD_ID, SomeLocatorsConfig.class);
    }

    private void registerPayloadTypes() {
        Payloads.registerType(ChangeTargetPointPayload.class);
        Payloads.registerType(CreateLodestonePointPayload.class);
        Payloads.registerType(OpenLocatorMenuPayload.class);
        Payloads.registerType(OpenLocatorInspectorMenuPayload.class);
        Payloads.registerType(RemovePointPayload.class);
        Payloads.registerType(RenamePointPayload.class);
        Payloads.registerType(SetLodestonePointPayload.class);
        Payloads.registerType(SetLocatorInspectorUnused.class);
        Payloads.registerType(WritePunchCardPayload.class);
    }
}
