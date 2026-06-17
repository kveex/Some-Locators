package me.kveex.somelocators.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import me.kveex.somelocators.SomeLocators;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record LodestonePointComponent(Optional<PointComponent> currentPoint, List<PointComponent> points, boolean skipPointCheck) implements TooltipProvider {
    public static final LodestonePointComponent DEFAULT = new LodestonePointComponent(Optional.empty(), List.of(), false);

    public static final Codec<LodestonePointComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                PointComponent.CODEC.optionalFieldOf("current_point").forGetter(LodestonePointComponent::currentPoint),
                Codec.list(PointComponent.CODEC).fieldOf("points").forGetter(LodestonePointComponent::points),
                Codec.BOOL.fieldOf("skip_check").forGetter(LodestonePointComponent::skipPointCheck)
    ).apply(builder, LodestonePointComponent::new));

    public static final StreamCodec<ByteBuf, LodestonePointComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(PointComponent.STREAM_CODEC),
            LodestonePointComponent::currentPoint,
            ByteBufCodecs.collection(ArrayList::new, PointComponent.STREAM_CODEC),
            LodestonePointComponent::points,
            ByteBufCodecs.BOOL,
            LodestonePointComponent::skipPointCheck,
            LodestonePointComponent::new
    );

    public LodestonePointComponent(PointComponent currentPoint, List<PointComponent> points, boolean skipPointCheck) {
        this(Optional.of(currentPoint), points, skipPointCheck);
    }

    public Optional<PointComponent> getTargetByGlobalPos(GlobalPos globalPos) {
        return points.stream().filter(target -> target.target().equals(globalPos)).findFirst();
    }

    public LodestonePointComponent forWorld(ServerLevel world) {
        if (this.currentPoint.isPresent() && !this.skipPointCheck) {
            if (this.currentPoint.get().target().dimension() != world.dimension()) {
                return this;
            } else {
                List<PointComponent> newPoints = new ArrayList<>(points);
                newPoints.remove(this.currentPoint.get());
                BlockPos blockPos = (this.currentPoint.get().target().pos());
                return world.isInWorldBounds(blockPos) && world.getBlockState(blockPos).equals(currentPoint.get().blockState())
                        ? this
                        : new LodestonePointComponent(Optional.empty(), newPoints, false);
            }
        } else {
            return this;
        }
    }

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext context, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag type, @NonNull DataComponentGetter components) {
        if (currentPoint.isPresent() && SomeLocators.CONFIG.locatorShowsAdditionalInformation()) {
            BlockPos targetPos = currentPoint.get().target().pos();
            String dimensionId = currentPoint.get().target().dimension().identifier().toString();

            consumer.accept(
                    Component.translatable("tooltip.some_locators.point_name",
                    currentPoint.get().name()).withStyle(ChatFormatting.DARK_GRAY)
            );

            consumer.accept(Component.translatable(
                    "tooltip.some_locators.point_position",
                    targetPos.getX(),
                    targetPos.getY(),
                    targetPos.getZ(),
                    dimensionId
            ).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
