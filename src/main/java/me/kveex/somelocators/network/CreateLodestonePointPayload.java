package me.kveex.somelocators.network;

import me.kveex.somelocators.network.util.ServerPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.GlobalPos;

public record CreateLodestonePointPayload(
        GlobalPos globalPos,
        BlockState blockState
) implements ServerPayload<CreateLodestonePointPayload> {
    public static final StreamCodec<RegistryFriendlyByteBuf, CreateLodestonePointPayload> CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, CreateLodestonePointPayload::globalPos,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CreateLodestonePointPayload::blockState,
            CreateLodestonePointPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CreateLodestonePointPayload> codec() {
        return CODEC;
    }
}
