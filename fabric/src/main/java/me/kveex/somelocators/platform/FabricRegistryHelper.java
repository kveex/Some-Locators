package me.kveex.somelocators.platform;

import com.mojang.serialization.Codec;
import me.kveex.somelocators.Constants;
import me.kveex.somelocators.platform.services.IRegistryHelper;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties properties) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        T item = itemFactory.apply(properties.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return () -> item;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        T block = blockFactory.apply(properties.setId(blockKey));

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        return () -> block;
    }

    @Override
    public Supplier<CreativeModeTab> registerCreativeTab(ResourceKey<CreativeModeTab> resourceKey, Supplier<CreativeModeTab> creativeTabFactory) {
        CreativeModeTab creativeModeTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, resourceKey, creativeTabFactory.get());
        return () -> creativeModeTab;
    }

    @Override
    public <T> Supplier<DataComponentType<T>> registerComponent(String name, Codec<T> codec) {
        DataComponentType<T> type = DataComponentType.<T>builder()
                .persistent(codec)
                .build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), type);
        return () -> type;
    }

    @Override
    public <T> Supplier<DataComponentType<T>> registerComponent(String name, Codec<T> codec, StreamCodec<FriendlyByteBuf, T> streamCodec) {
        DataComponentType<T> type = DataComponentType.<T>builder()
                .persistent(codec)
                .networkSynchronized(streamCodec)
                .build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), type);
        return () -> type;
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String name, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Supplier<? extends Block> block) {
        BlockEntityType<T> type = FabricBlockEntityTypeBuilder
                .create(blockEntityFactory::apply, block.get())
                .build();

        Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, name),
                type
        );

        return () -> type;
    }
}
