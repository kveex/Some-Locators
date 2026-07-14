package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.util.ClientPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.GlobalPos;

public record SetLodestonePointPayload(
        String name,
        GlobalPos globalPos,
        BlockState blockState
) implements ClientPayload<SetLodestonePointPayload> {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetLodestonePointPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetLodestonePointPayload::name,
            GlobalPos.STREAM_CODEC, SetLodestonePointPayload::globalPos,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), SetLodestonePointPayload::blockState,
            SetLodestonePointPayload::new
    );

    public PointComponent toPointComponent() {
        return new PointComponent(name, blockState, globalPos);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SetLodestonePointPayload> codec() {
        return CODEC;
    }
}
