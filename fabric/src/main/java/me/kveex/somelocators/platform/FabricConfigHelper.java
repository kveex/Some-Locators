package me.kveex.somelocators.platform;

import me.kveex.somelocators.config.SomeLocatorsConfig;
import me.kveex.somelocators.platform.services.IConfigHelper;

import java.util.function.Supplier;

public class FabricConfigHelper implements IConfigHelper {
    @Override
    public Supplier<Integer> maxLocatorPointsAmount() {
        return () -> SomeLocatorsConfig.maxLocatorPointsAmount;
    }

    @Override
    public Supplier<Boolean> locatorShowsAdditionalInformation() {
        return () -> SomeLocatorsConfig.locatorShowsAdditionalInformation;
    }

    @Override
    public Supplier<Boolean> isLocatorGlintDisabled() {
        return () -> SomeLocatorsConfig.isLocatorGlintDisabled;
    }

    @Override
    public Supplier<Integer> playerLocatorTrackingTime() {
        return () -> SomeLocatorsConfig.playerLocatorTrackingTime;
    }
}
