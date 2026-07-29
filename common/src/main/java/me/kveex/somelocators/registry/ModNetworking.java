package me.kveex.somelocators.registry;

import commonnetwork.api.Network;
import me.kveex.somelocators.block.LocatorTerminalBlock;
import me.kveex.somelocators.item.LocatorItem;
import me.kveex.somelocators.network.locator.*;
import me.kveex.somelocators.network.locatorterminal.OpenCopyScreenPayload;
import me.kveex.somelocators.network.locatorterminal.OpenInspectScreenPayload;
import me.kveex.somelocators.network.locatorterminal.SetUnusedPayload;
import me.kveex.somelocators.network.locatorterminal.WriteLodestoneComponentPayload;

public class ModNetworking {
    public static void init() {
        Network.registerPacket(SetUnusedPayload.type(), SetUnusedPayload.class, SetUnusedPayload.CODEC, LocatorTerminalBlock::setUnused);
        Network.registerPacket(WriteLodestoneComponentPayload.type(), WriteLodestoneComponentPayload.class, WriteLodestoneComponentPayload.CODEC, LocatorTerminalBlock::writePunchCard);
        Network.registerPacket(OpenInspectScreenPayload.type(), OpenInspectScreenPayload.class, OpenInspectScreenPayload.CODEC, OpenInspectScreenPayload::handler);
        Network.registerPacket(OpenCopyScreenPayload.type(), OpenCopyScreenPayload.class, OpenCopyScreenPayload.CODEC, OpenCopyScreenPayload::handler);
        Network.registerPacket(CreateLocatorPointPayload.type(), CreateLocatorPointPayload.class, CreateLocatorPointPayload.CODEC, CreateLocatorPointPayload::handler);
        Network.registerPacket(SetLocatorPointPayload.type(), SetLocatorPointPayload.class, SetLocatorPointPayload.CODEC, LocatorItem::setTracker);
        Network.registerPacket(ChangeTargetPointPayload.type(), ChangeTargetPointPayload.class, ChangeTargetPointPayload.CODEC, LocatorItem::changeTargetedPoint);
        Network.registerPacket(RenamePointPayload.type(), RenamePointPayload.class, RenamePointPayload.CODEC, LocatorItem::renamePoint);
        Network.registerPacket(RemovePointPayload.type(), RemovePointPayload.class, RemovePointPayload.CODEC, LocatorItem::removePoint);
        Network.registerPacket(OpenLocatorScreenPayload.type(), OpenLocatorScreenPayload.class, OpenLocatorScreenPayload.CODEC, OpenLocatorScreenPayload::handler);
    }
}
