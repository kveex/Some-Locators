package me.kveex.somelocators.network.locatorinspector;

import com.mojang.authlib.GameProfile;
import me.kveex.somelocators.network.util.ServerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record OpenInspectScreenPayload(
        BlockPos pos,
        ItemStack inspectedStack,
        Optional<GameProfile> playerProfile
) implements ServerPayload<OpenInspectScreenPayload>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenInspectScreenPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenInspectScreenPayload::pos,
            ItemStack.STREAM_CODEC, OpenInspectScreenPayload::inspectedStack,
            ByteBufCodecs.optional(ByteBufCodecs.GAME_PROFILE), OpenInspectScreenPayload::playerProfile,
            OpenInspectScreenPayload::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, OpenInspectScreenPayload> codec() {
        return CODEC;
    }
}
