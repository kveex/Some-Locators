package me.kveex.somelocators.registry;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;

public class ModEndecs {
    @SuppressWarnings("rawtypes")
    public static final StructEndec<RegistryKey> REGISTRY_KEY = StructEndecBuilder.of(
            MinecraftEndecs.IDENTIFIER.fieldOf("registry", RegistryKey::getRegistry),
            MinecraftEndecs.IDENTIFIER.fieldOf("value", RegistryKey::getValue),
            (registry, value) -> RegistryKey.of(RegistryKey.ofRegistry(registry), value)
    );

    public static final StructEndec<BlockState> BLOCK_STATE = StructEndecBuilder.of(
            Endec.INT.fieldOf("block_id", Block::getRawIdFromState),
            Block::getStateFromRawId
    );
}
