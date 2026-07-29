package me.kveex.somelocators.component;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.kveex.somelocators.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public record PlayerLocatorComponent(GameProfile gameProfile, PlayerDistance playerDistance, boolean tracked, long expiryTicks) implements TooltipProvider {
    public PlayerLocatorComponent(GameProfile gameProfile) {
        this(gameProfile, PlayerDistance.NOT_TRACKED, false, 0);
    }

    public static final Codec<PlayerLocatorComponent> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    ExtraCodecs.STORED_GAME_PROFILE.fieldOf("game_profile").forGetter(PlayerLocatorComponent::gameProfile),
                    PlayerDistance.CODEC.fieldOf("player_distance").forGetter(PlayerLocatorComponent::playerDistance),
                    Codec.BOOL.fieldOf("tracked").forGetter(PlayerLocatorComponent::tracked),
                    Codec.LONG.fieldOf("expiry_ticks").forGetter(PlayerLocatorComponent::expiryTicks)
            ).apply(builder, PlayerLocatorComponent::new)
    );

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext context, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag type, @NonNull DataComponentGetter components) {
        if (CommonConfig.locatorShowsAdditionalInformation) {
            consumer.accept(Component.translatable("ui.some_locators.locator_terminal_player_name").append(" ").append(gameProfile.name()).withStyle(ChatFormatting.DARK_GRAY));
            consumer.accept(Component.translatable("tooltip.some_locators.player_distance", playerDistance.asText().getString()).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
