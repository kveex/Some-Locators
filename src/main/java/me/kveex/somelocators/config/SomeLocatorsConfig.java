package me.kveex.somelocators.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class SomeLocatorsConfig extends MidnightConfig {
    @Entry(isSlider = true, min = 1, max = 56)
    public static int maxLocatorPointsAmount = 32;
    @Entry
    public static boolean locatorShowsAdditionalInformation = false;
    @Entry
    public static boolean isLocatorGlintDisabled = false;
    @Entry(min = 1, max = 86400)
    public static int playerLocatorTrackingTime = 300;
}
