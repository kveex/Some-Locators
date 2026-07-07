package me.kveex.somelocators.client;

import me.kveex.somelocators.client.ui.screen.LocatorInspectorScreen;
import me.kveex.somelocators.client.ui.screen.LocatorMenuScreen;
import me.kveex.somelocators.client.ui.screen.LocatorPointScreen;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.CreateLodestonePointPayload;
import me.kveex.somelocators.network.OpenLocatorInspectorMenuPayload;
import me.kveex.somelocators.network.OpenLocatorMenuPayload;
import me.kveex.somelocators.network.util.Payloads;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModNetworkingClient {
    public static void init() {
        Payloads.registerS2C(CreateLodestonePointPayload.class, ((createLodestonePoint, clientAccess) -> clientAccess.client().setScreen(new LocatorPointScreen(createLodestonePoint))));

        Payloads.registerS2C(OpenLocatorMenuPayload.class, ((openLocatorMenu, clientAccess) -> {
            LodestonePointComponent lodestonePointComponent = openLocatorMenu.lodestonePointComponent();
            if (lodestonePointComponent.currentPoint().isEmpty()) return;
            clientAccess.client().setScreen(new LocatorMenuScreen(lodestonePointComponent.currentPoint().get(), lodestonePointComponent.points()));
        }));

        Payloads.registerS2C(OpenLocatorInspectorMenuPayload.class, (((payload, clientAccess) -> clientAccess.client().setScreen(new LocatorInspectorScreen(payload)))));
    }
}
