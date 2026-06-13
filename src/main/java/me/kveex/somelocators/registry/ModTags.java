package me.kveex.somelocators.registry;

import me.kveex.somelocators.SomeLocators;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Block> TRACKABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(SomeLocators.MOD_ID, "trackable_blocks"));

    public static void init() {}
}
