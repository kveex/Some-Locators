package me.kveex.somelocators.network.locatorterminal;

import me.kveex.somelocators.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SetUnusedPayload(
        BlockPos pos,
        boolean blockMoved
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "set_unused_payload");
    public static final StreamCodec<RegistryFriendlyByteBuf, SetUnusedPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetUnusedPayload::pos,
            ByteBufCodecs.BOOL, SetUnusedPayload::blockMoved,
            SetUnusedPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }
}
