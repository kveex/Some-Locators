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

public record BetterLodestoneTrackerComponent(Optional<LodestoneTarget> currentTarget, List<LodestoneTarget> targets) implements TooltipAppender {
    public static final BetterLodestoneTrackerComponent DEFAULT = new BetterLodestoneTrackerComponent(Optional.empty(), List.of());
    public static final Codec<BetterLodestoneTrackerComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                LodestoneTarget.CODEC.optionalFieldOf("current_target").forGetter(BetterLodestoneTrackerComponent::currentTarget),
                Codec.list(LodestoneTarget.CODEC).fieldOf("targets").forGetter(BetterLodestoneTrackerComponent::targets)
    ).apply(builder, BetterLodestoneTrackerComponent::new));

    public Optional<LodestoneTarget> getTargetByGlobalPos(GlobalPos globalPos) {
        return targets.stream().filter(target -> target.target().equals(globalPos)).findFirst();
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> consumer, TooltipType type, ComponentsAccess components) {
        if (currentTarget.isPresent() && BetterCoordination.CONFIG.locatorShowsAdditionalInformation()) {
            BlockPos targetPos = currentTarget.get().target().pos();
            String dimensionId = currentTarget.get().target().dimension().getRegistry().toString();

            consumer.accept(
                    Text.translatable("item.better_coordination.target_name",
                    currentTarget.get().name()).formatted(Formatting.DARK_GRAY)
            );

            consumer.accept(Text.translatable(
                    "item.better_coordination.target_position",
                    targetPos.getX(),
                    targetPos.getY(),
                    targetPos.getZ(),
                    dimensionId
            ).formatted(Formatting.DARK_GRAY));
        }
    }
}
