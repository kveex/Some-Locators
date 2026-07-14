package me.kveex.somelocators.network.locatorinspector;

import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SetUnusedPayload(
        BlockPos pos,
        boolean blockMoved
) implements ClientPayload<SetUnusedPayload>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, SetUnusedPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetUnusedPayload::pos,
            ByteBufCodecs.BOOL, SetUnusedPayload::blockMoved,
            SetUnusedPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SetUnusedPayload> codec() {
        return CODEC;
    }
}
