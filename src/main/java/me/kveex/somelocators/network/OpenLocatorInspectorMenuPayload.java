package me.kveex.somelocators.network;

import me.kveex.somelocators.network.util.ServerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record OpenLocatorInspectorMenuPayload(
        ItemStack firstLocator,
        ItemStack secondLocator,
        BlockPos pos
) implements ServerPayload<OpenLocatorInspectorMenuPayload>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenLocatorInspectorMenuPayload> CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, OpenLocatorInspectorMenuPayload::firstLocator,
            ItemStack.OPTIONAL_STREAM_CODEC, OpenLocatorInspectorMenuPayload::secondLocator,
            BlockPos.STREAM_CODEC, OpenLocatorInspectorMenuPayload::pos,
            OpenLocatorInspectorMenuPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, OpenLocatorInspectorMenuPayload> codec() {
        return CODEC;
    }
}
