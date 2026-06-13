package me.kveex.somelocators.registry;

import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import me.kveex.somelocators.SomeLocators;
import net.minecraft.util.Identifier;

public class ModGroups {
    public static final OwoItemGroup GROUP = OwoItemGroup.builder(
            Identifier.of(SomeLocators.MOD_ID, "item_group"),
            () -> Icon.of(ModItems.LOCATOR_ITEM.getDefaultStack())
    ).build();

    public static void init() {
        GROUP.initialize();
    }
}
