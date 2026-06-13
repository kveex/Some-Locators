package me.kveex.somelocators.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;
import me.kveex.somelocators.SomeLocators;

@Modmenu(modId = SomeLocators.MOD_ID)
@Config(name = "some_locators_config", wrapperName = "ModConfig")
@SuppressWarnings("unused")
public class ModConfigModel {
    @RangeConstraint(min = 1, max = 128)
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public int maxLocatorPointsAmount = 32;
    public boolean locatorShowsAdditionalInformation = false;
    public boolean isLocatorGlintDisabled = false;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RangeConstraint(min = 1, max = 86400)
    public int playerLocatorTrackingTime = 300;
}
