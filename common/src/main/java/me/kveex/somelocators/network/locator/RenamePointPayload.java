package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.Constants;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record RenamePointPayload(
        int currentPage,
        String newName,
        BlockState blockState,
        GlobalPos globalPos,
        LodestoneTracker tracker
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "rename_point");
    public static final StreamCodec<RegistryFriendlyByteBuf, RenamePointPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RenamePointPayload::currentPage,
            ByteBufCodecs.STRING_UTF8, RenamePointPayload::newName,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),RenamePointPayload::blockState,
            GlobalPos.STREAM_CODEC, RenamePointPayload::globalPos,
            LodestoneTracker.STREAM_CODEC, RenamePointPayload::tracker,
            RenamePointPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }
}
