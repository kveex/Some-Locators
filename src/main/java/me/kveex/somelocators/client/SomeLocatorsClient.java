package me.kveex.somelocators.client;

import net.fabricmc.api.ClientModInitializer;

public class SomeLocatorsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModNetworkingClient.init();
    }
}
