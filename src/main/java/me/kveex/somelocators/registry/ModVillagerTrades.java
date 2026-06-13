package me.kveex.somelocators.registry;

import me.kveex.somelocators.item.LocatorItem;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

import java.util.Optional;

public class ModVillagerTrades {
    public static void init() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 5, factories -> factories.add((world, entity, random) -> new TradeOffer(
                new TradedItem(Items.EMERALD, 20),
                Optional.of(new TradedItem(Items.COMPASS, 1)),
                LocatorItem.locatorForTrade(world, entity, random),
                1, 4, 0.04f
        )));
    }
}
