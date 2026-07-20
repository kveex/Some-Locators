package me.kveex.somelocators.network.locator;

import commonnetwork.networking.data.PacketContext;
import me.kveex.somelocators.Constants;
import me.kveex.somelocators.client.ui.screen.locator.LocatorPointScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record CreateLodestonePointPayload(
        GlobalPos globalPos,
        BlockState blockState
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "create_lodestone_point_payload");
    public static final StreamCodec<RegistryFriendlyByteBuf, CreateLodestonePointPayload> CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, CreateLodestonePointPayload::globalPos,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CreateLodestonePointPayload::blockState,
            CreateLodestonePointPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }

    public static void handler(PacketContext<CreateLodestonePointPayload> context) {
        Minecraft.getInstance().setScreen(new LocatorPointScreen(context.message()));
    }
}
