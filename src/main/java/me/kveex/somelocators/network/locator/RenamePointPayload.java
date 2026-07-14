package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RenamePointPayload(
        String newName,
        PointComponent renamedPoint
) implements ClientPayload<RenamePointPayload> {
    public static final StreamCodec<RegistryFriendlyByteBuf, RenamePointPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, RenamePointPayload::newName,
            PointComponent.STREAM_CODEC, RenamePointPayload::renamedPoint,
            RenamePointPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RenamePointPayload> codec() {
        return CODEC;
    }
}
