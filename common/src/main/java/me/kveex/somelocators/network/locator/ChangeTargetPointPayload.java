package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.Constants;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ChangeTargetPointPayload(
        PointComponent newTarget
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "change_target_point");
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeTargetPointPayload> CODEC = StreamCodec.composite(
            PointComponent.STREAM_CODEC, ChangeTargetPointPayload::newTarget,
            ChangeTargetPointPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }
}
