package me.kveex.somelocators.registry;

import me.kveex.somelocators.block.entity.LocatorTerminalBlockEntity;
import me.kveex.somelocators.platform.Services;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final Supplier<BlockEntityType<LocatorTerminalBlockEntity>> LOCATOR_TERMINAL_BLOCK_ENTITY = Services.REGISTRY.registerBlockEntityType(
            "locator_terminal", LocatorTerminalBlockEntity::new, ModBlocks.LOCATOR_TERMINAL
    );

    public static void init() {}
}
