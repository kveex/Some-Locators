package me.kveex.somelocators.network;

import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ChangeTargetPoint(
        PointComponent newTarget
) implements ClientPayload<ChangeTargetPoint> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeTargetPoint> CODEC = StreamCodec.composite(
            PointComponent.STREAM_CODEC, ChangeTargetPoint::newTarget,
            ChangeTargetPoint::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ChangeTargetPoint> codec() {
        return CODEC;
    }
}
