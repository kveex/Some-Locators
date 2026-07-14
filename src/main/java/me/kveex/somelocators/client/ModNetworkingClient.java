package me.kveex.somelocators.client;

import me.kveex.somelocators.client.ui.screen.locator.LocatorMenuScreen;
import me.kveex.somelocators.client.ui.screen.locator.LocatorPointScreen;
import me.kveex.somelocators.client.ui.screen.locatorinspector.LocatorInspectorCopyScreen;
import me.kveex.somelocators.client.ui.screen.locatorinspector.LocatorInspectorInspectScreen;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.locator.CreateLodestonePointPayload;
import me.kveex.somelocators.network.locatorinspector.OpenCopyScreenPayload;
import me.kveex.somelocators.network.locatorinspector.OpenInspectScreenPayload;
import me.kveex.somelocators.network.locator.OpenLocatorMenuPayload;
import me.kveex.somelocators.network.util.Payloads;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModNetworkingClient {
    public static void init() {
        Payloads.registerS2C(CreateLodestonePointPayload.class, ((createLodestonePoint, clientAccess) -> clientAccess.client().setScreen(new LocatorPointScreen(createLodestonePoint))));
        Payloads.registerS2C(OpenLocatorMenuPayload.class, ((payload, clientAccess) -> {
            LodestonePointComponent lodestonePointComponent = payload.lodestonePointComponent();
            if (lodestonePointComponent.currentPoint().isEmpty()) return;
            clientAccess.client().setScreen(new LocatorMenuScreen(lodestonePointComponent.currentPoint().get(), lodestonePointComponent.points()));
        }));
        Payloads.registerS2C(OpenInspectScreenPayload.class, (((payload, clientAccess) -> clientAccess.client().setScreen(new LocatorInspectorInspectScreen(payload)))));
        Payloads.registerS2C(OpenCopyScreenPayload.class, (payload, clientAccess) -> clientAccess.client().setScreen(new LocatorInspectorCopyScreen(payload)));
    }
}
