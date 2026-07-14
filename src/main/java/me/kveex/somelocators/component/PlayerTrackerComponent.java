package me.kveex.somelocators.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.kveex.somelocators.config.SomeLocatorsConfig;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.function.Consumer;

public record PlayerTrackerComponent(String trackedPlayer, PlayerDistance playerDistance, boolean tracked, long expiryTicks) implements TooltipProvider {
    public PlayerTrackerComponent(UUID trackedPlayerUUID) {
        this(trackedPlayerUUID.toString(), PlayerDistance.NOT_TRACKED, false, 0);
    }

    public PlayerTrackerComponent(UUID trackedPlayerUUID, PlayerDistance playerDistance, boolean tracked, long expiryTicks) {
        this(trackedPlayerUUID.toString(), playerDistance, tracked, expiryTicks);
    }

    public static final Codec<PlayerTrackerComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Codec.STRING.fieldOf("tracker_player_uuid").forGetter(PlayerTrackerComponent::trackedPlayer),
                    PlayerDistance.CODEC.fieldOf("player_distance").forGetter(PlayerTrackerComponent::playerDistance),
                    Codec.BOOL.fieldOf("tracked").forGetter(PlayerTrackerComponent::tracked),
                    Codec.LONG.fieldOf("expiry_ticks").forGetter(PlayerTrackerComponent::expiryTicks)
            ).apply(builder, PlayerTrackerComponent::new)
    );

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext context, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag type, @NonNull DataComponentGetter components) {
        if (SomeLocatorsConfig.locatorShowsAdditionalInformation) {
            Component playerDistanceText = Component.translatable("tooltip.some_locators.player_distance")
                    .append(Component.literal(" ")).append(playerDistance.asText()).withStyle(ChatFormatting.DARK_GRAY);

            consumer.accept(playerDistanceText);

            consumer.accept(Component.literal("UUID: " + trackedPlayer).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public UUID trackedPlayerUUID() {
        return UUID.fromString(trackedPlayer);
    }
}
