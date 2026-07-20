package me.kveex.somelocators.registry;

import me.kveex.somelocators.block.entity.LocatorInspectorBlockEntity;
import me.kveex.somelocators.platform.Services;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final Supplier<BlockEntityType<LocatorInspectorBlockEntity>> LOCATOR_INSPECTOR_BLOCK_ENTITY = Services.REGISTRY.registerBlockEntityType(
            "locator_inspector", LocatorInspectorBlockEntity::new, ModBlocks.LOCATOR_INSPECTOR
    );

    public static void init() {}
}
