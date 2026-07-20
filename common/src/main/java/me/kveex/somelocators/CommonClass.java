package me.kveex.somelocators;

import me.kveex.somelocators.registry.*;

public class CommonClass {
    public static void init() {
        ModNetworking.init();
        ModComponents.init();
        ModTags.init();
        ModBlocks.init();
        ModItems.init();
        ModBlockEntityTypes.init();
        ModCreativeTabs.init();
    }
}