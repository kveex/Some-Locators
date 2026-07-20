package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.Constants;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record SetLodestonePointPayload(
        String name,
        GlobalPos globalPos,
        BlockState blockState
)  {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "set_lodestone_point_payload");
    public static final StreamCodec<RegistryFriendlyByteBuf, SetLodestonePointPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetLodestonePointPayload::name,
            GlobalPos.STREAM_CODEC, SetLodestonePointPayload::globalPos,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), SetLodestonePointPayload::blockState,
            SetLodestonePointPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }

    public PointComponent toPointComponent() {
        return new PointComponent(name, blockState, globalPos);
    }
}
