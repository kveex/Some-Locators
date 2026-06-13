package me.kveex.somelocators.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.kveex.somelocators.SomeLocators;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record LodestonePointComponent(Optional<PointComponent> currentPoint, List<PointComponent> points, boolean skipPointCheck) implements TooltipAppender {
    public static final LodestonePointComponent DEFAULT = new LodestonePointComponent(Optional.empty(), List.of(), false);

    public static final Codec<LodestonePointComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                PointComponent.CODEC.optionalFieldOf("current_point").forGetter(LodestonePointComponent::currentPoint),
                Codec.list(PointComponent.CODEC).fieldOf("points").forGetter(LodestonePointComponent::points),
                Codec.BOOL.fieldOf("skip_check").forGetter(LodestonePointComponent::skipPointCheck)
    ).apply(builder, LodestonePointComponent::new));

    public LodestonePointComponent(PointComponent currentPoint, List<PointComponent> points, boolean skipPointCheck) {
        this(Optional.of(currentPoint), points, skipPointCheck);
    }

    public Optional<PointComponent> getTargetByGlobalPos(GlobalPos globalPos) {
        return points.stream().filter(target -> target.target().equals(globalPos)).findFirst();
    }

    public LodestonePointComponent forWorld(ServerWorld world) {
        if (this.currentPoint.isPresent() && !this.skipPointCheck) {
            if (this.currentPoint.get().target().dimension() != world.getRegistryKey()) {
                return this;
            } else {
                List<PointComponent> newPoints = new ArrayList<>(points);
                newPoints.remove(this.currentPoint.get());
                BlockPos blockPos = (this.currentPoint.get().target().pos());
                return world.isInBuildLimit(blockPos) && world.getBlockState(blockPos).equals(currentPoint.get().blockState())
                        ? this
                        : new LodestonePointComponent(Optional.empty(), newPoints, false);
            }
        } else {
            return this;
        }
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> consumer, TooltipType type, ComponentsAccess components) {
        if (currentPoint.isPresent() && SomeLocators.CONFIG.locatorShowsAdditionalInformation()) {
            BlockPos targetPos = currentPoint.get().target().pos();
            String dimensionId = currentPoint.get().target().dimension().getValue().toString();

            consumer.accept(
                    Text.translatable("tooltip.some_locators.point_name",
                    currentPoint.get().name()).formatted(Formatting.DARK_GRAY)
            );

            consumer.accept(Text.translatable(
                    "tooltip.some_locators.point_position",
                    targetPos.getX(),
                    targetPos.getY(),
                    targetPos.getZ(),
                    dimensionId
            ).formatted(Formatting.DARK_GRAY));
        }
    }
}
