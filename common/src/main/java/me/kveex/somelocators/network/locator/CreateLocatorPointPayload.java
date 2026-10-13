package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.Constants;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record CreateLocatorPointPayload(
        GlobalPos globalPos,
        BlockState blockState
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "create_lodestone_point_payload");
    public static final StreamCodec<RegistryFriendlyByteBuf, CreateLocatorPointPayload> CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, CreateLocatorPointPayload::globalPos,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CreateLocatorPointPayload::blockState,
            CreateLocatorPointPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }
}
