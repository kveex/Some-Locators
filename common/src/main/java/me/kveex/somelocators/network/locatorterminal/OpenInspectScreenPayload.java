package me.kveex.somelocators.network.locatorterminal;

import me.kveex.somelocators.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record OpenInspectScreenPayload(
        BlockPos pos,
        ItemStack inspectedStack
)
{
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "open_inspect_screen");
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenInspectScreenPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenInspectScreenPayload::pos,
            ItemStack.STREAM_CODEC, OpenInspectScreenPayload::inspectedStack,
            OpenInspectScreenPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }
}
