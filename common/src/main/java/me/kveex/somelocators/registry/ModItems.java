package me.kveex.somelocators.registry;

import me.kveex.somelocators.item.LocatorItem;
import me.kveex.somelocators.item.PlayerLocatorItem;
import me.kveex.somelocators.platform.Services;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ModItems {
    public static final Supplier<Item> PUNCH_CARD_ITEM = Services.REGISTRY.registerItem(
            "punch_card",
            Item::new,
            new Item.Properties()
    );

    public static final Supplier<Item> LOCATOR_ITEM = Services.REGISTRY.registerItem(
            "locator",
            LocatorItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Supplier<Item> PLAYER_LOCATOR_ITEM = Services.REGISTRY.registerItem(
            "player_locator",
            PlayerLocatorItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Supplier<Item> LOCATOR_INSPECTOR_SCREEN_ITEM = Services.REGISTRY.registerItem(
            "locator_inspector_screen",
            Item::new,
            new Item.Properties()
    );

    public static void init() {}
}
