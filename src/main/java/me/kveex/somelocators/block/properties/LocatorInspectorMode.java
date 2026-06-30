package me.kveex.somelocators.block.properties;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public enum LocatorInspectorMode implements StringRepresentable {
    EMPTY,
    INSPECT,
    COPY;

    @Override
    public @NonNull String getSerializedName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public LocatorInspectorMode getNextMode() {
        return switch (this) {
            case EMPTY -> LocatorInspectorMode.INSPECT;
            case INSPECT, COPY -> LocatorInspectorMode.COPY;
        };
    }

    public LocatorInspectorMode getPreviousMode() {
        return switch (this) {
            case COPY -> LocatorInspectorMode.INSPECT;
            case INSPECT, EMPTY -> LocatorInspectorMode.EMPTY;
        };
    }
}
