package me.kveex.somelocators.network;

import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RemovePointPayload(
        PointComponent removedPoint
) implements ClientPayload<RemovePointPayload> {
    public static final StreamCodec<RegistryFriendlyByteBuf, RemovePointPayload> CODEC = StreamCodec.composite(
            PointComponent.STREAM_CODEC, RemovePointPayload::removedPoint,
            RemovePointPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RemovePointPayload> codec() {
        return CODEC;
    }
}
