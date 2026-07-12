package me.kveex.somelocators.network;

import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record WritePunchCardPayload(
        ItemStack punchCard,
        BlockPos locatorInspectorBlockPos
) implements ClientPayload<WritePunchCardPayload>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, WritePunchCardPayload> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, WritePunchCardPayload::punchCard,
            BlockPos.STREAM_CODEC, WritePunchCardPayload::locatorInspectorBlockPos,
            WritePunchCardPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WritePunchCardPayload> codec() {
        return CODEC;
    }
}
