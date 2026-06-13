package me.kveex.somelocators.packet;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.GlobalPos;

public record CreateLodestonePoint(
        GlobalPos globalPos,
        BlockState blockState
) {}
