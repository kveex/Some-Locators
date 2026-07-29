package me.kveex.somelocators.registry;

import me.kveex.somelocators.Constants;
import me.kveex.somelocators.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final ResourceKey<CreativeModeTab> SOME_LOCATORS_CREATIVE_MODE_TAB_RESOURCE_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Constants.MOD_ID, "creative_tab")
    );

    public static void init() {
        Services.REGISTRY.registerCreativeTab(
                SOME_LOCATORS_CREATIVE_MODE_TAB_RESOURCE_KEY,
                () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                        .icon(() -> new ItemStack(ModItems.LOCATOR_ITEM.get()))
                        .displayItems((params, output) -> {
                            output.accept(ModItems.LOCATOR_ITEM.get());
                            output.accept(ModItems.PLAYER_LOCATOR_ITEM.get());
                            output.accept(ModBlocks.LOCATOR_TERMINAL.get());
                            output.accept(ModItems.PUNCH_CARD_ITEM.get());
                            output.accept(ModItems.LOCATOR_TERMINAL_SCREEN_ITEM.get());
                        })
                        .title(Component.translatable("itemGroup.some_locators.item_group"))
                        .build()
        );
    }
}
