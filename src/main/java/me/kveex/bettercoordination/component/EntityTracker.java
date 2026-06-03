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

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public record EntityTracker(Optional<String> trackedEntity, EntityDistance entityDistance) implements TooltipAppender {
    public EntityTracker(UUID trackedEntityUUID) {
        this(Optional.of(trackedEntityUUID.toString()), EntityDistance.HERE);
    }

    public EntityTracker(UUID trackedEntityUUID, EntityDistance entityDistance) {
        this(Optional.of(trackedEntityUUID.toString()), entityDistance);
    }

    public static final Codec<EntityTracker> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Codec.STRING.optionalFieldOf("tracker_entity_uuid").forGetter(EntityTracker::trackedEntity),
                    EntityDistance.CODEC.fieldOf("entity_distance").forGetter(EntityTracker::entityDistance)
            ).apply(builder, EntityTracker::new)
    );

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> consumer, TooltipType type, ComponentsAccess components) {
        if (trackedEntity.isPresent() && BetterCoordination.CONFIG.locatorShowsAdditionalInformation()) {
            Text playerDistanceText = Text.translatable("tooltip.better_coordination.player_distance")
                    .append(Text.literal(" ")).append(entityDistance.asText()).formatted(Formatting.DARK_GRAY);

            Text entityDistanceText = Text.translatable("tooltip.better_coordination.entity_distance")
                    .append(Text.literal(" ")).append(entityDistance.asText()).formatted(Formatting.DARK_GRAY);

            Text distanceText = BetterCoordination.CONFIG.canPlayerLocatorTracksAllEntities() ?
                    entityDistanceText : playerDistanceText;

            consumer.accept(distanceText);

            consumer.accept(Text.literal("UUID: " + trackedEntity.get()).formatted(Formatting.DARK_GRAY));
        }
    }

    public Optional<UUID> trackedEntityUUID() {
        return trackedEntity.map(UUID::fromString);
    }
}
