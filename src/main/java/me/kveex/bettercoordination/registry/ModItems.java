package me.kveex.bettercoordination.registry;

import me.kveex.bettercoordination.BetterCoordination;
import me.kveex.bettercoordination.component.BetterLodestoneTrackerComponent;
import me.kveex.bettercoordination.item.LocatorItem;
import me.kveex.bettercoordination.item.PlayerLocatorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static final Item LOCATOR = registerItem(
            "locator",
            LocatorItem::new,
            new Item.Settings().component(
                    ModComponents.BETTER_TRACKER_COMPONENT,
                    BetterLodestoneTrackerComponent.DEFAULT
            )
    );

    public static final Item PLAYER_LOCATOR = registerItem(
            "player_locator",
            PlayerLocatorItem::new,
            new Item.Settings()
    );

    private static  <T extends Item> T registerItem(String name, Function<Item.Settings, T> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BetterCoordination.MOD_ID, name));

        T item = itemFactory.apply(settings.registryKey(itemKey));

        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void init() {}
}
