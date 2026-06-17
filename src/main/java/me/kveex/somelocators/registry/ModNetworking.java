package me.kveex.somelocators.registry;

import me.kveex.somelocators.item.LocatorItem;
import me.kveex.somelocators.network.*;
import me.kveex.somelocators.network.util.Payloads;

public class ModNetworking {

    public static void init() {
        Payloads.registerC2S(SetLodestonePoint.class, LocatorItem::setTracker);
        Payloads.registerC2S(ChangeTargetPoint.class, LocatorItem::changeTargetedPoint);
        Payloads.registerC2S(RemovePoint.class, LocatorItem::removePoint);
        Payloads.registerC2S(RenamePoint.class, LocatorItem::renamePoint);
    }
}
