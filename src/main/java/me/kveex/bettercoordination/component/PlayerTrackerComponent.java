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

public record PlayerTrackerComponent(Optional<String> trackedPlayer, PlayerDistance playerDistance, boolean tracked, long expiryTicks) implements TooltipAppender {
    public PlayerTrackerComponent(UUID trackedPlayerUUID) {
        this(Optional.of(trackedPlayerUUID.toString()), PlayerDistance.NOT_TRACKED, false, 0);
    }

    public PlayerTrackerComponent(UUID trackedPlayerUUID, PlayerDistance playerDistance, boolean tracked, long expiryTicks) {
        this(Optional.of(trackedPlayerUUID.toString()), playerDistance, tracked, expiryTicks);
    }

    public static final Codec<PlayerTrackerComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Codec.STRING.optionalFieldOf("tracker_player_uuid").forGetter(PlayerTrackerComponent::trackedPlayer),
                    PlayerDistance.CODEC.fieldOf("player_distance").forGetter(PlayerTrackerComponent::playerDistance),
                    Codec.BOOL.fieldOf("tracked").forGetter(PlayerTrackerComponent::tracked),
                    Codec.LONG.fieldOf("expiry_ticks").forGetter(PlayerTrackerComponent::expiryTicks)
            ).apply(builder, PlayerTrackerComponent::new)
    );

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> consumer, TooltipType type, ComponentsAccess components) {
        if (trackedPlayer.isPresent() && BetterCoordination.CONFIG.locatorShowsAdditionalInformation()) {
            Text playerDistanceText = Text.translatable("tooltip.better_coordination.player_distance")
                    .append(Text.literal(" ")).append(playerDistance.asText()).formatted(Formatting.DARK_GRAY);

            consumer.accept(playerDistanceText);

            consumer.accept(Text.literal("UUID: " + trackedPlayer.get()).formatted(Formatting.DARK_GRAY));
        }
    }

    public Optional<UUID> trackedPlayerUUID() {
        return trackedPlayer.map(UUID::fromString);
    }
}
