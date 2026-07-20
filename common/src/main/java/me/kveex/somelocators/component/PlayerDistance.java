package me.kveex.somelocators.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum PlayerDistance implements StringRepresentable {
    NOT_FOUND("not_found", "distance.some_locators.not_found", 0),
    NOT_TRACKED("not_tracked", "distance.some_locators.not_tracked", 0),
    IN_ANOTHER_DIMENSION("in_another_dimension", "distance.some_locators.in_another_dimension", 0),
    REALLY_FAR("really_far", "distance.some_locators.really_far", 1024),
    FAR("far", "distance.some_locators.far", 256),
    NEAR("near", "distance.some_locators.near", 96),
    CLOSE("close", "distance.some_locators.close", 32),
    HERE("here", "distance.some_locators.here", 6);

    private final String name;
    private final String translationKey;
    private final double maxDistance;
    public static final Codec<PlayerDistance> CODEC = StringRepresentable.fromEnumWithMapping(PlayerDistance::values, String::toLowerCase);

    PlayerDistance(String componentName, String translationKey, double maxDistance) {
        this.name = componentName;
        this.translationKey = translationKey;
        this.maxDistance = maxDistance;
    }

    @Override
    public @NonNull String getSerializedName() {
        return name;
    }

    public Component asText() {
        return Component.translatable(translationKey);
    }

    public static PlayerDistance fromDistance(double distance) {
        if (distance <= HERE.maxDistance) {
            return HERE;
        } else if (distance <= CLOSE.maxDistance) {
            return CLOSE;
        } else if (distance <= NEAR.maxDistance) {
            return NEAR;
        } else if (distance <= FAR.maxDistance) {
            return FAR;
        } else {
            return REALLY_FAR;
        }
    }
}

