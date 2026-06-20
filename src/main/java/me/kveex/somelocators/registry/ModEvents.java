package me.kveex.somelocators.registry;

import me.kveex.somelocators.item.PlayerLocatorItem;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class ModEvents {
    public static void init() {
        // For Player Locator to work on interactable entities, like villagers, horses, etc.
        UseEntityCallback.EVENT.register(PlayerLocatorItem::trySetTrackedPlayer);
    }
}
