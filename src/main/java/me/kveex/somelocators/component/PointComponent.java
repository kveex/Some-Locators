package me.kveex.somelocators.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.core.GlobalPos;

import java.util.Optional;

public record PointComponent(String name, BlockState blockState, GlobalPos target, LodestoneTracker lodestoneTracker) {
    public PointComponent(String name, BlockState blockState, GlobalPos target) {
        this(name, blockState, target, new LodestoneTracker(Optional.of(target), true));
    }

    public static final Codec<PointComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Codec.STRING.fieldOf("name").forGetter(PointComponent::name),
                    BlockState.CODEC.fieldOf("block_state").forGetter(PointComponent::blockState),
                    GlobalPos.CODEC.fieldOf("target").forGetter(PointComponent::target),
                    LodestoneTracker.CODEC.fieldOf("lodestone_tracker").forGetter(PointComponent::lodestoneTracker)
            ).apply(builder, PointComponent::new)
    );

    public static final StreamCodec<ByteBuf, PointComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PointComponent::name,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), PointComponent::blockState,
            GlobalPos.STREAM_CODEC, PointComponent::target,
            LodestoneTracker.STREAM_CODEC, PointComponent::lodestoneTracker,
            PointComponent::new
    );
}
