package me.kveex.bettercoordination.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.util.math.GlobalPos;

import java.util.Optional;

public record PointComponent(String name, BlockState blockState, GlobalPos target, LodestoneTrackerComponent lodestoneTracker) {
    public PointComponent(String name, BlockState blockState, GlobalPos target) {
        this(name, blockState, target, new LodestoneTrackerComponent(Optional.of(target), true));
    }

    public static final Codec<PointComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Codec.STRING.fieldOf("name").forGetter(PointComponent::name),
                    BlockState.CODEC.fieldOf("block_state").forGetter(PointComponent::blockState),
                    GlobalPos.CODEC.fieldOf("target").forGetter(PointComponent::target),
                    LodestoneTrackerComponent.CODEC.fieldOf("lodestone_tracker").forGetter(PointComponent::lodestoneTracker)
            ).apply(builder, PointComponent::new)
    );
}
