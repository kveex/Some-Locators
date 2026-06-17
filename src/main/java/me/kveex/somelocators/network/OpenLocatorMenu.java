package me.kveex.somelocators.network;

import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.util.ServerPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record OpenLocatorMenu(
        LodestonePointComponent lodestonePointComponent
) implements ServerPayload<OpenLocatorMenu> {
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenLocatorMenu> CODEC = StreamCodec.composite(
            LodestonePointComponent.STREAM_CODEC, OpenLocatorMenu::lodestonePointComponent,
            OpenLocatorMenu::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, OpenLocatorMenu> codec() {
        return CODEC;
    }
}
