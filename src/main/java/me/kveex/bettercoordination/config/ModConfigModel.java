package me.kveex.bettercoordination.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;
import me.kveex.bettercoordination.BetterCoordination;

@Modmenu(modId = BetterCoordination.MOD_ID)
@Config(name = "better_coordination_config", wrapperName = "ModConfig")
@Sync(Option.SyncMode.OVERRIDE_CLIENT)
public class ModConfigModel {
    @RangeConstraint(min = 1, max = 128)
    public int maxLocatorPointsAmount = 32;
    public boolean canPlayerLocatorTracksAllEntities = false;
    public boolean locatorShowsAdditionalInformation = false;
    public boolean isLocatorGlintDisabled = false;
    @RangeConstraint(min = 1, max = 86400)
    public int playerLocatorTrackingTime = 300;
}
