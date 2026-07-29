package me.kveex.somelocators.registry;

import me.kveex.somelocators.block.LocatorTerminalBlock;
import me.kveex.somelocators.platform.Services;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ModBlocks {
    public static final Supplier<LocatorTerminalBlock> LOCATOR_TERMINAL = Services.REGISTRY.registerBlock(
            "locator_terminal",
            LocatorTerminalBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OBSERVER)
    );

    public static void init() {}
}
