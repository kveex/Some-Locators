package me.kveex.somelocators.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record LocatorComponent(Optional<PointComponent> currentPoint, List<PointComponent> points, boolean skipPointCheck) {
    public static final LocatorComponent DEFAULT = new LocatorComponent(Optional.empty(), List.of(), false);

    public static final Codec<LocatorComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                PointComponent.CODEC.optionalFieldOf("current_point").forGetter(LocatorComponent::currentPoint),
                Codec.list(PointComponent.CODEC).fieldOf("points").forGetter(LocatorComponent::points),
                Codec.BOOL.fieldOf("skip_check").forGetter(LocatorComponent::skipPointCheck)
    ).apply(builder, LocatorComponent::new));

    public static final StreamCodec<FriendlyByteBuf, LocatorComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(PointComponent.STREAM_CODEC),
            LocatorComponent::currentPoint,
            ByteBufCodecs.collection(ArrayList::new, PointComponent.STREAM_CODEC),
            LocatorComponent::points,
            ByteBufCodecs.BOOL,
            LocatorComponent::skipPointCheck,
            LocatorComponent::new
    );

    public LocatorComponent(PointComponent currentPoint, List<PointComponent> points, boolean skipPointCheck) {
        this(Optional.of(currentPoint), points, skipPointCheck);
    }

    public Optional<PointComponent> getTargetByGlobalPos(GlobalPos globalPos) {
        return points.stream().filter(target -> target.target().equals(globalPos)).findFirst();
    }

    public LocatorComponent forWorld(ServerLevel world) {
        if (this.currentPoint.isPresent() && !this.skipPointCheck) {
            if (this.currentPoint.get().target().dimension() != world.dimension()) {
                return this;
            } else {
                List<PointComponent> newPoints = new ArrayList<>(points);
                newPoints.remove(this.currentPoint.get());
                BlockPos blockPos = (this.currentPoint.get().target().pos());
                return world.isInWorldBounds(blockPos) && world.getBlockState(blockPos).equals(currentPoint.get().blockState())
                        ? this
                        : new LocatorComponent(Optional.empty(), newPoints, false);
            }
        } else {
            return this;
        }
    }
}
