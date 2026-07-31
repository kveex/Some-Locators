package me.kveex.somelocators;

import eu.midnightdust.lib.config.MidnightConfig;
import me.kveex.somelocators.item.LocatorItem;
import me.kveex.somelocators.registry.ModComponents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Optional;

public class SomeLocators implements ModInitializer {
    
    @Override
    public void onInitialize() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 5, factories -> factories.add((world, entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 20),
                Optional.of(new ItemCost(Items.COMPASS, 1)),
                LocatorItem.locatorForTrade(world, entity, random),
                1, 4, 0.04f
        )));
        MidnightConfig.init(Constants.MOD_ID, CommonConfig.class);
        CommonClass.init();

        ComponentTooltipAppenderRegistry.addAfter(DataComponents.DAMAGE, ModComponents.PLAYER_LOCATOR_COMPONENT.get());
    }
}
