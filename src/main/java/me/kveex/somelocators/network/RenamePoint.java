package me.kveex.somelocators.network;

import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RenamePoint(
        String newName,
        PointComponent renamedPoint
) implements ClientPayload<RenamePoint> {
    public static final StreamCodec<RegistryFriendlyByteBuf, RenamePoint> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, RenamePoint::newName,
            PointComponent.STREAM_CODEC, RenamePoint::renamedPoint,
            RenamePoint::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RenamePoint> codec() {
        return CODEC;
    }
}
