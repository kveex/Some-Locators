package me.kveex.somelocators.network.locatorinspector;

import commonnetwork.networking.data.PacketContext;
import me.kveex.somelocators.Constants;
import me.kveex.somelocators.client.ui.screen.locatorinspector.LocatorInspectorCopyScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record OpenCopyScreenPayload(
        BlockPos pos,
        ItemStack locator,
        ItemStack punchCard
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "open_copy_screen");
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCopyScreenPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenCopyScreenPayload::pos,
            ItemStack.STREAM_CODEC, OpenCopyScreenPayload::locator,
            ItemStack.STREAM_CODEC, OpenCopyScreenPayload::punchCard,
            OpenCopyScreenPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }

    public static void handler(PacketContext<OpenCopyScreenPayload> context) {
        Minecraft.getInstance().setScreen(new LocatorInspectorCopyScreen(context.message()));
    }
}
