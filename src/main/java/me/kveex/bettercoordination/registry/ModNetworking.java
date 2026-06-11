package me.kveex.bettercoordination.registry;

import io.wispforest.owo.network.OwoNetChannel;
import me.kveex.bettercoordination.BetterCoordination;
import me.kveex.bettercoordination.item.LocatorItem;
import me.kveex.bettercoordination.packet.*;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final OwoNetChannel MOD_CHANNEL = OwoNetChannel
            .create(Identifier.of(BetterCoordination.MOD_ID, "main"));

    public static void init() {
        MOD_CHANNEL.addEndecs(builder -> {
            builder.register(ModEndecs.REGISTRY_KEY, RegistryKey.class);
            builder.register(ModEndecs.BLOCK_STATE, BlockState.class);
        });

        MOD_CHANNEL.registerClientboundDeferred(CreateLodestonePoint.class);
        MOD_CHANNEL.registerClientboundDeferred(OpenLocatorMenu.class);
        MOD_CHANNEL.registerServerbound(SetLodestonePoint.class, LocatorItem::setTracker);
        MOD_CHANNEL.registerServerbound(ChangeTargetPoint.class, LocatorItem::changeTargetedPoint);
        MOD_CHANNEL.registerServerbound(RemovePoint.class, LocatorItem::removePoint);
        MOD_CHANNEL.registerServerbound(RenamePoint.class, LocatorItem::renamePoint);
    }
}
