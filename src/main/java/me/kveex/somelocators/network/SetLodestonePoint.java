package me.kveex.somelocators.network;

import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.GlobalPos;

public record SetLodestonePoint(
        String name,
        GlobalPos globalPos,
        BlockState blockState
) implements ClientPayload<SetLodestonePoint> {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetLodestonePoint> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetLodestonePoint::name,
            GlobalPos.STREAM_CODEC, SetLodestonePoint::globalPos,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), SetLodestonePoint::blockState,
            SetLodestonePoint::new
    );

    public PointComponent toPointComponent() {
        return new PointComponent(name, blockState, globalPos);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SetLodestonePoint> codec() {
        return CODEC;
    }
}
