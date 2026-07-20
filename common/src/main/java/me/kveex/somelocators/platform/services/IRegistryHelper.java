package me.kveex.somelocators.platform.services;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.*;

public interface IRegistryHelper {
    <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties properties);
    <T extends Block> Supplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties);
    Supplier<CreativeModeTab> registerCreativeTab(ResourceKey<CreativeModeTab> resourceKey, Supplier<CreativeModeTab> creativeTabFactory);
    <T> Supplier<DataComponentType<T>> registerComponent(String name, Codec<T> codec);
    <T> Supplier<DataComponentType<T>> registerComponent(String name, Codec<T> codec, StreamCodec<FriendlyByteBuf, T> streamCodec);
    <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String name, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Supplier<? extends Block> block);
}
