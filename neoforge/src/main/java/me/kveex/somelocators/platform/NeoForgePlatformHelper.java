package me.kveex.somelocators.platform;

import me.kveex.somelocators.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public Loaders getPlatform() {
        return Loaders.NEOFORGE;
    }
}