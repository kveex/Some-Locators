package me.kveex.somelocators.network.locator;

import commonnetwork.networking.data.PacketContext;
import me.kveex.somelocators.Constants;
import me.kveex.somelocators.client.ui.screen.locator.LocatorMenuScreen;
import me.kveex.somelocators.component.LocatorComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenLocatorScreenPayload(
        LocatorComponent locatorComponent
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "open_locator_menu");
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenLocatorScreenPayload> CODEC = StreamCodec.composite(
            LocatorComponent.STREAM_CODEC, OpenLocatorScreenPayload::locatorComponent,
            OpenLocatorScreenPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }

    public static void handler(PacketContext<OpenLocatorScreenPayload> context) {
        LocatorComponent locatorComponent = context.message().locatorComponent();
        if (locatorComponent.currentPoint().isEmpty()) return;
        Minecraft.getInstance().setScreen(new LocatorMenuScreen(locatorComponent.currentPoint().get(), locatorComponent.points()));
    }
}
