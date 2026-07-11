package me.kveex.somelocators.client;

import me.kveex.somelocators.client.ui.screen.LocatorInspectorCopyScreen;
import me.kveex.somelocators.client.ui.screen.LocatorInspectorInspectScreen;
import me.kveex.somelocators.client.ui.screen.LocatorMenuScreen;
import me.kveex.somelocators.client.ui.screen.LocatorPointScreen;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.CreateLodestonePointPayload;
import me.kveex.somelocators.network.OpenLocatorInspectorMenuPayload;
import me.kveex.somelocators.network.OpenLocatorMenuPayload;
import me.kveex.somelocators.network.util.Payloads;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public class ModNetworkingClient {
    public static void init() {
        Payloads.registerS2C(CreateLodestonePointPayload.class, ((createLodestonePoint, clientAccess) -> clientAccess.client().setScreen(new LocatorPointScreen(createLodestonePoint))));

        Payloads.registerS2C(OpenLocatorMenuPayload.class, ((payload, clientAccess) -> {
            LodestonePointComponent lodestonePointComponent = payload.lodestonePointComponent();
            if (lodestonePointComponent.currentPoint().isEmpty()) return;
            clientAccess.client().setScreen(new LocatorMenuScreen(lodestonePointComponent.currentPoint().get(), lodestonePointComponent.points()));
        }));

        Payloads.registerS2C(OpenLocatorInspectorMenuPayload.class, (((payload, clientAccess) -> {
            Screen openScreen;
            if (!payload.locator().isEmpty() && !payload.punchCard().isEmpty()) {
                openScreen = new LocatorInspectorCopyScreen(payload);
            } else {
                openScreen = new LocatorInspectorInspectScreen(payload);
            }
            clientAccess.client().setScreen(openScreen);
        })));
    }
}
