package me.kveex.bettercoordination.packet;

import me.kveex.bettercoordination.component.PointComponent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.GlobalPos;

public record SetLodestonePoint(
        String name,
        GlobalPos globalPos,
        BlockState blockState
) {
    public PointComponent toPointComponent() {
        return new PointComponent(name, blockState, globalPos);
    }
}
