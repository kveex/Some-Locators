package me.kveex.somelocators.network.locatorinspector;

import me.kveex.somelocators.network.util.ServerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record OpenInspectScreenPayload(
        BlockPos pos,
        ItemStack inspectedStack
) implements ServerPayload<OpenInspectScreenPayload>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenInspectScreenPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenInspectScreenPayload::pos,
            ItemStack.STREAM_CODEC, OpenInspectScreenPayload::inspectedStack,
            OpenInspectScreenPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, OpenInspectScreenPayload> codec() {
        return CODEC;
    }
}
