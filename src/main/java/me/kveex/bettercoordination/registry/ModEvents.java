package me.kveex.bettercoordination.registry;

import me.kveex.bettercoordination.item.LocatorItem;
import me.kveex.bettercoordination.item.PlayerLocatorItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class ModEvents {
    public static void init() {
        // For Player Locator to work on interactable entities, like villagers, horses, etc.
        UseEntityCallback.EVENT.register(PlayerLocatorItem::trySetTrackedPlayer);
        // For Locator to work on interactable blocks
        UseBlockCallback.EVENT.register(LocatorItem::useOnBlock);
    }
}
