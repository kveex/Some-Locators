package me.kveex.somelocators.network;

import me.kveex.somelocators.network.util.ServerPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.GlobalPos;

public record CreateLodestonePoint(
        GlobalPos globalPos,
        BlockState blockState
) implements ServerPayload<CreateLodestonePoint> {
    public static final StreamCodec<RegistryFriendlyByteBuf, CreateLodestonePoint> CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, CreateLodestonePoint::globalPos,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CreateLodestonePoint::blockState,
            CreateLodestonePoint::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CreateLodestonePoint> codec() {
        return CODEC;
    }
}
