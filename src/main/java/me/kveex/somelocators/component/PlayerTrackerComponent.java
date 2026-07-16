package me.kveex.somelocators.component;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.kveex.somelocators.config.SomeLocatorsConfig;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public record PlayerTrackerComponent(GameProfile gameProfile, PlayerDistance playerDistance, boolean tracked, long expiryTicks) implements TooltipProvider {
    public PlayerTrackerComponent(GameProfile gameProfile) {
        this(gameProfile, PlayerDistance.NOT_TRACKED, false, 0);
    }

    public static final Codec<PlayerTrackerComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    ExtraCodecs.STORED_GAME_PROFILE.fieldOf("game_profile").forGetter(PlayerTrackerComponent::gameProfile),
                    PlayerDistance.CODEC.fieldOf("player_distance").forGetter(PlayerTrackerComponent::playerDistance),
                    Codec.BOOL.fieldOf("tracked").forGetter(PlayerTrackerComponent::tracked),
                    Codec.LONG.fieldOf("expiry_ticks").forGetter(PlayerTrackerComponent::expiryTicks)
            ).apply(builder, PlayerTrackerComponent::new)
    );

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext context, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag type, @NonNull DataComponentGetter components) {
        if (SomeLocatorsConfig.locatorShowsAdditionalInformation) {
            consumer.accept(Component.translatable("ui.some_locators.locator_inspector_player_name").append(" ").append(gameProfile.name()).withStyle(ChatFormatting.DARK_GRAY));
            consumer.accept(Component.translatable("tooltip.some_locators.player_distance", playerDistance.asText().getString()).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
