package me.kveex.somelocators.registry;

import me.kveex.somelocators.block.LocatorInspectorBlock;
import me.kveex.somelocators.platform.Services;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ModBlocks {
    public static final Supplier<LocatorInspectorBlock> LOCATOR_INSPECTOR = Services.REGISTRY.registerBlock(
            "locator_inspector",
            LocatorInspectorBlock::new,
            BlockBehaviour.Properties.of().sound(SoundType.METAL)
    );

    public static void init() {}
}
