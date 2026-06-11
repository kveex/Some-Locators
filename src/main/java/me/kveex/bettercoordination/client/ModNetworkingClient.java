package me.kveex.bettercoordination.client;

import me.kveex.bettercoordination.client.ui.LocatorMenuScreen;
import me.kveex.bettercoordination.client.ui.NewLocatorPointScreen;
import me.kveex.bettercoordination.component.LodestonePointComponent;
import me.kveex.bettercoordination.packet.CreateLodestonePoint;
import me.kveex.bettercoordination.packet.OpenLocatorMenu;
import me.kveex.bettercoordination.registry.ModNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModNetworkingClient {
    public static void init() {
        ModNetworking.MOD_CHANNEL.registerClientbound(CreateLodestonePoint.class, (message, access) -> access.runtime().setScreen(new NewLocatorPointScreen(message)));

        ModNetworking.MOD_CHANNEL.registerClientbound(OpenLocatorMenu.class, (message, access) -> {
            LodestonePointComponent lodestonePointComponent = message.lodestonePointComponent();
            if (lodestonePointComponent.currentPoint().isEmpty()) return;
            access.runtime().setScreen(new LocatorMenuScreen(lodestonePointComponent.currentPoint().get(), lodestonePointComponent.points()));
        });
    }
}
