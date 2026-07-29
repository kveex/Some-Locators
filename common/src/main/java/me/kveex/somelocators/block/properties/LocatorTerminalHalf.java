package me.kveex.somelocators.block.properties;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public enum LocatorTerminalHalf implements StringRepresentable {
    TOP,
    BOTTOM;

    @Override
    public @NonNull String getSerializedName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
