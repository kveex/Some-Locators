package me.kveex.somelocators;

import com.mojang.authlib.GameProfile;
import eu.midnightdust.lib.config.MidnightConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.kveex.somelocators.component.PlayerDistance;
import me.kveex.somelocators.component.PlayerLocatorComponent;
import me.kveex.somelocators.item.LocatorItem;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

@Mod(Constants.MOD_ID)
public class SomeLocators {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);

    public SomeLocators(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
        BLOCKS.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
        ITEMS.register(eventBus);
        NeoForge.EVENT_BUS.addListener(SomeLocators::registerVillagerTrades);
        NeoForge.EVENT_BUS.addListener(SomeLocators::registerTooltips);
        MidnightConfig.init(Constants.MOD_ID, CommonConfig.class);
        CommonClass.init();
    }

    @SubscribeEvent
    private static void registerVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.CARTOGRAPHER) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.get(5).add((level, entity, random) -> new MerchantOffer(
                        new ItemCost(Items.EMERALD, 20),
                        Optional.of(new ItemCost(Items.COMPASS, 1)),
                        LocatorItem.locatorForTrade(level, entity, random),
                        1, 4, 0.04f
                )
            );
        }
    }

    @SubscribeEvent
    private static void registerTooltips(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        Optional<PlayerLocatorComponent> optional = Optional.ofNullable(itemStack.get(ModComponents.PLAYER_LOCATOR_COMPONENT));

        if (optional.isPresent() && CommonConfig.locatorShowsAdditionalInformation) {
            GameProfile gameProfile = optional.get().gameProfile();
            PlayerDistance playerDistance = optional.get().playerDistance();

            Component playerName = Component.translatable("ui.some_locators.locator_terminal_player_name").append(" ").append(gameProfile.name()).withStyle(ChatFormatting.DARK_GRAY);
            Component distance = Component.translatable("tooltip.some_locators.player_distance", playerDistance.asText().getString()).withStyle(ChatFormatting.DARK_GRAY);

            event.getToolTip().addAll(List.of(playerName, distance));
        }
    }
}