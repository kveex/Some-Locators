package me.kveex.somelocators.platform.services;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    Loaders getPlatform();

    default boolean isPlatform(Loaders loader) {
        return getPlatform() == loader;
    }

    enum Loaders {
        FABRIC,
        NEOFORGE
    }
}