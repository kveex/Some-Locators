package me.kveex.somelocators.network.locatorinspector;

import me.kveex.somelocators.network.util.ServerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record OpenCopyScreenPayload(
        BlockPos pos,
        ItemStack locator,
        ItemStack punchCard
) implements ServerPayload<OpenCopyScreenPayload> {
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCopyScreenPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenCopyScreenPayload::pos,
            ItemStack.STREAM_CODEC, OpenCopyScreenPayload::locator,
            ItemStack.STREAM_CODEC, OpenCopyScreenPayload::punchCard,
            OpenCopyScreenPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, OpenCopyScreenPayload> codec() {
        return CODEC;
    }
}
