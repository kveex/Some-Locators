package me.kveex.somelocators.registry;

import commonnetwork.api.Network;
import me.kveex.somelocators.block.LocatorInspectorBlock;
import me.kveex.somelocators.item.LocatorItem;
import me.kveex.somelocators.network.locator.*;
import me.kveex.somelocators.network.locatorinspector.OpenCopyScreenPayload;
import me.kveex.somelocators.network.locatorinspector.OpenInspectScreenPayload;
import me.kveex.somelocators.network.locatorinspector.SetUnusedPayload;
import me.kveex.somelocators.network.locatorinspector.WriteLodestoneComponentPayload;

public class ModNetworking {
    public static void init() {
        Network.registerPacket(SetUnusedPayload.type(), SetUnusedPayload.class, SetUnusedPayload.CODEC, LocatorInspectorBlock::setUnused);
        Network.registerPacket(WriteLodestoneComponentPayload.type(), WriteLodestoneComponentPayload.class, WriteLodestoneComponentPayload.CODEC, LocatorInspectorBlock::writePunchCard);
        Network.registerPacket(OpenInspectScreenPayload.type(), OpenInspectScreenPayload.class, OpenInspectScreenPayload.CODEC, OpenInspectScreenPayload::handler);
        Network.registerPacket(OpenCopyScreenPayload.type(), OpenCopyScreenPayload.class, OpenCopyScreenPayload.CODEC, OpenCopyScreenPayload::handler);
        Network.registerPacket(CreateLodestonePointPayload.type(), CreateLodestonePointPayload.class, CreateLodestonePointPayload.CODEC, CreateLodestonePointPayload::handler);
        Network.registerPacket(SetLodestonePointPayload.type(), SetLodestonePointPayload.class, SetLodestonePointPayload.CODEC, LocatorItem::setTracker);
        Network.registerPacket(ChangeTargetPointPayload.type(), ChangeTargetPointPayload.class, ChangeTargetPointPayload.CODEC, LocatorItem::changeTargetedPoint);
        Network.registerPacket(RenamePointPayload.type(), RenamePointPayload.class, RenamePointPayload.CODEC, LocatorItem::renamePoint);
        Network.registerPacket(RemovePointPayload.type(), RemovePointPayload.class, RemovePointPayload.CODEC, LocatorItem::removePoint);
        Network.registerPacket(OpenLocatorMenuPayload.type(), OpenLocatorMenuPayload.class, OpenLocatorMenuPayload.CODEC, OpenLocatorMenuPayload::handler);
    }
}
