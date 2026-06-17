package me.kveex.somelocators.network;

import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RemovePoint(
        PointComponent removedPoint
) implements ClientPayload<RemovePoint> {
    public static final StreamCodec<RegistryFriendlyByteBuf, RemovePoint> CODEC = StreamCodec.composite(
            PointComponent.STREAM_CODEC, RemovePoint::removedPoint,
            RemovePoint::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RemovePoint> codec() {
        return CODEC;
    }
}
