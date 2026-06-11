package me.kveex.bettercoordination.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.kveex.bettercoordination.BetterCoordination;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record LodestonePointComponent(Optional<PointComponent> currentPoint, List<PointComponent> points) implements TooltipAppender {
    public static final LodestonePointComponent DEFAULT = new LodestonePointComponent(Optional.empty(), List.of());

    public static final Codec<LodestonePointComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                PointComponent.CODEC.optionalFieldOf("current_point").forGetter(LodestonePointComponent::currentPoint),
                Codec.list(PointComponent.CODEC).fieldOf("points").forGetter(LodestonePointComponent::points)
    ).apply(builder, LodestonePointComponent::new));

    public LodestonePointComponent(PointComponent currentPoint, List<PointComponent> points) {
        this(Optional.of(currentPoint), points);
    }

    public Optional<PointComponent> getTargetByGlobalPos(GlobalPos globalPos) {
        return points.stream().filter(target -> target.target().equals(globalPos)).findFirst();
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> consumer, TooltipType type, ComponentsAccess components) {
        if (currentPoint.isPresent() && BetterCoordination.CONFIG.locatorShowsAdditionalInformation()) {
            BlockPos targetPos = currentPoint.get().target().pos();
            String dimensionId = currentPoint.get().target().dimension().getValue().toString();

            consumer.accept(
                    Text.translatable("tooltip.better_coordination.point_name",
                    currentPoint.get().name()).formatted(Formatting.DARK_GRAY)
            );

            consumer.accept(Text.translatable(
                    "tooltip.better_coordination.point_position",
                    targetPos.getX(),
                    targetPos.getY(),
                    targetPos.getZ(),
                    dimensionId
            ).formatted(Formatting.DARK_GRAY));
        }
    }
}
