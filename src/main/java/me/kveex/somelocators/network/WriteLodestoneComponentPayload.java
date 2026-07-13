package me.kveex.somelocators.network;

import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record WriteLodestoneComponentPayload(
        ItemStack writtenStack,
        BlockPos locatorInspectorBlockPos
) implements ClientPayload<WriteLodestoneComponentPayload>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, WriteLodestoneComponentPayload> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, WriteLodestoneComponentPayload::writtenStack,
            BlockPos.STREAM_CODEC, WriteLodestoneComponentPayload::locatorInspectorBlockPos,
            WriteLodestoneComponentPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WriteLodestoneComponentPayload> codec() {
        return CODEC;
    }
}
