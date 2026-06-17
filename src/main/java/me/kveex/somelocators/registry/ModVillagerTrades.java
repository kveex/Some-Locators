package me.kveex.somelocators.registry;

import me.kveex.somelocators.item.LocatorItem;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

import java.util.Optional;

public class ModVillagerTrades {
    public static void init() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 5, factories -> factories.add((world, entity, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 20),
                Optional.of(new ItemCost(Items.COMPASS, 1)),
                LocatorItem.locatorForTrade(world, entity, random),
                1, 4, 0.04f
        )));
    }
}
