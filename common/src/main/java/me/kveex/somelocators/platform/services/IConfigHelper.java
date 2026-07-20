package me.kveex.somelocators.platform.services;

import java.util.function.Supplier;

public interface IConfigHelper {
    Supplier<Integer> maxLocatorPointsAmount();
    Supplier<Boolean> locatorShowsAdditionalInformation();
    Supplier<Boolean> isLocatorGlintDisabled();
    Supplier<Integer> playerLocatorTrackingTime();
}
