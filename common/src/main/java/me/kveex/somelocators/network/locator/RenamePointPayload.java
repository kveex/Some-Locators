package me.kveex.somelocators.network.locator;

import me.kveex.somelocators.Constants;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RenamePointPayload(
        String newName,
        PointComponent renamedPoint
) {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "rename_point");
    public static final StreamCodec<RegistryFriendlyByteBuf, RenamePointPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, RenamePointPayload::newName,
            PointComponent.STREAM_CODEC, RenamePointPayload::renamedPoint,
            RenamePointPayload::new
    );

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(ID);
    }
}
