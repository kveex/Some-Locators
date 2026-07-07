package me.kveex.somelocators.network;

import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.util.ServerPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record OpenLocatorMenuPayload(
        LodestonePointComponent lodestonePointComponent
) implements ServerPayload<OpenLocatorMenuPayload> {
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenLocatorMenuPayload> CODEC = StreamCodec.composite(
            LodestonePointComponent.STREAM_CODEC, OpenLocatorMenuPayload::lodestonePointComponent,
            OpenLocatorMenuPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, OpenLocatorMenuPayload> codec() {
        return CODEC;
    }
}
