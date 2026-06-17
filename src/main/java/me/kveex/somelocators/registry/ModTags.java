package me.kveex.somelocators.registry;

import me.kveex.somelocators.SomeLocators;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;

public class ModTags {
    public static final TagKey<Block> TRACKABLE_BLOCKS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "trackable_blocks"));

    public static void init() {}
}
