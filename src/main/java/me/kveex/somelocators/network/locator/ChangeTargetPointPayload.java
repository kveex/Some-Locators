package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ChangeTargetPointPayload(
        PointComponent newTarget
) implements ClientPayload<ChangeTargetPointPayload> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeTargetPointPayload> CODEC = StreamCodec.composite(
            PointComponent.STREAM_CODEC, ChangeTargetPointPayload::newTarget,
            ChangeTargetPointPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ChangeTargetPointPayload> codec() {
        return CODEC;
    }
}
