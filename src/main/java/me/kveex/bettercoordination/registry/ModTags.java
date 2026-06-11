package me.kveex.bettercoordination.registry;

import me.kveex.bettercoordination.BetterCoordination;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Block> TRACKABLE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(BetterCoordination.MOD_ID, "trackable_blocks"));

    public static void init() {}
}
