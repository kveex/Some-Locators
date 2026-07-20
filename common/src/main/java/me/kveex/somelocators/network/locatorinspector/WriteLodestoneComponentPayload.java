package me.kveex.somelocators.network.locatorinspector;

import me.kveex.somelocators.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record WriteLodestoneComponentPayload(
        ItemStack writtenStack,
        BlockPos locatorInspectorBlockPos
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "write_lodestone_component_payload");
    public static final StreamCodec<RegistryFriendlyByteBuf, WriteLodestoneComponentPayload> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, WriteLodestoneComponentPayload::writtenStack,
            BlockPos.STREAM_CODEC, WriteLodestoneComponentPayload::locatorInspectorBlockPos,
            WriteLodestoneComponentPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }
}
