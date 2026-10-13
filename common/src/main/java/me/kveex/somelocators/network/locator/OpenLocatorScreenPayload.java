package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.Constants;
import me.kveex.somelocators.component.LocatorComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenLocatorScreenPayload(
        LocatorComponent locatorComponent,
        int page
) {
    public OpenLocatorScreenPayload(LocatorComponent locatorComponent) {
        this(locatorComponent, 0);
    }

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "open_locator_menu");
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenLocatorScreenPayload> CODEC = StreamCodec.composite(
            LocatorComponent.STREAM_CODEC, OpenLocatorScreenPayload::locatorComponent,
            ByteBufCodecs.INT, OpenLocatorScreenPayload::page,
            OpenLocatorScreenPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }
}
