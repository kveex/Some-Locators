package me.kveex.somelocators.network;

import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SetLocatorInspectorUnused(
        BlockPos pos,
        boolean blockMoved
) implements ClientPayload<SetLocatorInspectorUnused>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, SetLocatorInspectorUnused> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetLocatorInspectorUnused::pos,
            ByteBufCodecs.BOOL, SetLocatorInspectorUnused::blockMoved,
            SetLocatorInspectorUnused::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SetLocatorInspectorUnused> codec() {
        return CODEC;
    }
}
