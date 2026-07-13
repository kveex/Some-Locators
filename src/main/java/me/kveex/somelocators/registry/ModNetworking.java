package me.kveex.somelocators.registry;

import me.kveex.somelocators.block.LocatorInspectorBlock;
import me.kveex.somelocators.item.LocatorItem;
import me.kveex.somelocators.network.*;
import me.kveex.somelocators.network.util.Payloads;

public class ModNetworking {

    public static void init() {
        Payloads.registerC2S(SetLodestonePointPayload.class, LocatorItem::setTracker);
        Payloads.registerC2S(ChangeTargetPointPayload.class, LocatorItem::changeTargetedPoint);
        Payloads.registerC2S(RemovePointPayload.class, LocatorItem::removePoint);
        Payloads.registerC2S(RenamePointPayload.class, LocatorItem::renamePoint);
        Payloads.registerC2S(SetLocatorInspectorUnused.class, LocatorInspectorBlock::setUnused);
        Payloads.registerC2S(WriteLodestoneComponentPayload.class, LocatorInspectorBlock::writePunchCard);
    }
}
