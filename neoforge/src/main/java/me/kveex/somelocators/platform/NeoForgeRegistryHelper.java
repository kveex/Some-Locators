package me.kveex.somelocators.platform;

import com.mojang.serialization.Codec;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.platform.services.IRegistryHelper;
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
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties properties) {
        return SomeLocators.ITEMS.registerItem(
                name,
                itemFactory,
                () -> properties
        );
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties) {
        DeferredBlock<T> block = SomeLocators.BLOCKS.registerBlock(
                name,
                blockFactory,
                () -> properties
        );
        SomeLocators.ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    @Override
    public Supplier<CreativeModeTab> registerCreativeTab(ResourceKey<CreativeModeTab> resourceKey, Supplier<CreativeModeTab> creativeTabFactory) {
        return SomeLocators.CREATIVE_MODE_TABS.register(resourceKey.identifier().getNamespace(), creativeTabFactory);
    }

    @Override
    public <T> Supplier<DataComponentType<T>> registerComponent(String name, Codec<T> codec) {
        return SomeLocators.DATA_COMPONENTS.registerComponentType(
                name,
                builder -> builder.persistent(codec)
        );
    }

    @Override
    public <T> Supplier<DataComponentType<T>> registerComponent(String name, Codec<T> codec, StreamCodec<FriendlyByteBuf, T> streamCodec) {
        return SomeLocators.DATA_COMPONENTS.registerComponentType(
                name,
                builder -> builder.persistent(codec).networkSynchronized(streamCodec)
        );
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String name, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Supplier<? extends Block> block) {
        return SomeLocators.BLOCK_ENTITY_TYPES.register(
                name,
                () -> new BlockEntityType<>(
                        blockEntityFactory::apply,
                        false,
                        block.get()
                )
        );
    }
}
