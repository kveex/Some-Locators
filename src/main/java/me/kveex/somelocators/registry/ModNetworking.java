package me.kveex.somelocators.registry;

import io.wispforest.owo.network.OwoNetChannel;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.item.LocatorItem;
import me.kveex.somelocators.packet.*;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final OwoNetChannel MOD_CHANNEL = OwoNetChannel
            .create(Identifier.of(SomeLocators.MOD_ID, "main"));

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
