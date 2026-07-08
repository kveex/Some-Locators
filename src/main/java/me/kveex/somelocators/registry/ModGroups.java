package me.kveex.somelocators.registry;

import me.kveex.somelocators.SomeLocators;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModGroups {
    public static final ResourceKey<CreativeModeTab> SOME_LOCATORS_CREATIVE_MODE_TAB_RESOURCE_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "creative_tab")
    );

    public static final CreativeModeTab SOME_LOCATORS_CREATIVE_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.LOCATOR_ITEM))
            .title(Component.translatable("itemGroup.some_locators.item_group"))
            .displayItems((params, output) -> {
               output.accept(ModItems.LOCATOR_ITEM);
               output.accept(ModItems.PLAYER_LOCATOR_ITEM);
               output.accept(ModBlocks.LOCATOR_INSPECTOR);
               output.accept(ModItems.PUNCH_CARD_ITEM);
            })
            .build();

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, SOME_LOCATORS_CREATIVE_MODE_TAB_RESOURCE_KEY, SOME_LOCATORS_CREATIVE_TAB);
    }
}
