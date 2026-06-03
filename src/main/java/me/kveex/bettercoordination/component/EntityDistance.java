package me.kveex.bettercoordination.component;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum EntityDistance implements StringIdentifiable {
    NOT_TRACKED("not_tracked", "distance.better_coordination.not_tracked", 0),
    REALLY_FAR("really_far", "distance.better_coordination.really_far", 1024),
    FAR("far", "distance.better_coordination.far", 256),
    NEAR("near", "distance.better_coordination.near", 96),
    CLOSE("close", "distance.better_coordination.close", 32),
    HERE("here", "distance.better_coordination.here", 6);

    private final String name;
    private final String translationKey;
    private final double maxDistance;
    public static final Codec<EntityDistance> CODEC = StringIdentifiable.createCodec(EntityDistance::values, String::toLowerCase);

    EntityDistance(String componentName, String translationKey, double maxDistance) {
        this.name = componentName;
        this.translationKey = translationKey;
        this.maxDistance = maxDistance;
    }

    @Override
    public String asString() {
        return name;
    }

    public Text asText() {
        return Text.translatable(translationKey);
    }

    public static EntityDistance fromDistance(double distance) {
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

