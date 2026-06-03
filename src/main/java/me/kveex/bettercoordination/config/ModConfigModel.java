package me.kveex.bettercoordination.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = "bettercoordination")
@Config(name = "better_coordination_config", wrapperName = "ModConfig")
@Sync(Option.SyncMode.OVERRIDE_CLIENT)
public class ModConfigModel {
    @RangeConstraint(min = 1, max = 128)
    public int maxLocatorTargetsAmount = 32;
    public boolean canPlayerLocatorTracksAllEntities = false;
    public boolean locatorShowsAdditionalInformation = false;
    public boolean isLocatorGlintDisabled = false;
}
