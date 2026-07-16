package me.kveex.somelocators.registry;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.item.LocatorItem;
import me.kveex.somelocators.item.PlayerLocatorItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static final Item LOCATOR_ITEM = registerItem(
            "locator",
            LocatorItem::new,
            new Item.Properties().component(
                    ModComponents.LODESTONE_POINT_COMPONENT,
                    LodestonePointComponent.DEFAULT
            ).stacksTo(64)
    );

    public static final Item PLAYER_LOCATOR_ITEM = registerItem(
            "player_locator",
            PlayerLocatorItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item PUNCH_CARD_ITEM = registerItem(
            "punch_card",
            Item::new,
            new Item.Properties().stacksTo(16)
    );

    public static final Item LOCATOR_INSPECTOR_SCREEN_ITEM = registerItem(
            "locator_inspector_screen",
            Item::new,
            new Item.Properties()
    );

    private static  <T extends Item> T registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, name));

        T item = itemFactory.apply(settings.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void init() {}
}
