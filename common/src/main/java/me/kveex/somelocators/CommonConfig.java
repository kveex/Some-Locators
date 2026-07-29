package me.kveex.somelocators;

import eu.midnightdust.lib.config.MidnightConfig;

public class CommonConfig extends MidnightConfig {
    @Entry(isSlider = true, min = 1, max = 56)
    @Server
    public static int maxLocatorPointsAmount = 32;
    @Entry
    public static boolean locatorShowsAdditionalInformation = false;
    @Entry
    public static boolean isLocatorGlintDisabled = false;
    @Entry(min = 1, max = 86400)
    @Server
    public static int playerLocatorTrackingTime = 300;
}
