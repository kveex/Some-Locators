package me.kveex.somelocators.client;

import me.kveex.somelocators.client.ui.LocatorMenuScreen;
import me.kveex.somelocators.client.ui.NewLocatorPointScreen;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.CreateLodestonePoint;
import me.kveex.somelocators.network.OpenLocatorMenu;
import me.kveex.somelocators.network.util.Payloads;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModNetworkingClient {
    public static void init() {
        Payloads.registerS2C(CreateLodestonePoint.class, ((createLodestonePoint, clientAccess) -> clientAccess.runtime().setScreen(new NewLocatorPointScreen(createLodestonePoint))));

        Payloads.registerS2C(OpenLocatorMenu.class, ((openLocatorMenu, clientAccess) -> {
            LodestonePointComponent lodestonePointComponent = openLocatorMenu.lodestonePointComponent();
            if (lodestonePointComponent.currentPoint().isEmpty()) return;
            clientAccess.runtime().setScreen(new LocatorMenuScreen(lodestonePointComponent.currentPoint().get(), lodestonePointComponent.points()));
        }));
    }
}
