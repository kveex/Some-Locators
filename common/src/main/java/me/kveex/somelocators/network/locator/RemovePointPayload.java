package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.Constants;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RemovePointPayload(
        PointComponent removedPoint
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "remove_point");
    public static final StreamCodec<RegistryFriendlyByteBuf, RemovePointPayload> CODEC = StreamCodec.composite(
            PointComponent.STREAM_CODEC, RemovePointPayload::removedPoint,
            RemovePointPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }

}
