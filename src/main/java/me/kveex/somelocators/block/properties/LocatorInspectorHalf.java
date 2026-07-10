package me.kveex.somelocators.block.properties;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public enum LocatorInspectorHalf implements StringRepresentable {
    TOP,
    BOTTOM;

    public LocatorInspectorHalf getOpposite() {
        return this == TOP ? BOTTOM : TOP;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
