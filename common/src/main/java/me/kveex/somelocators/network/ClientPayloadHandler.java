package me.kveex.somelocators.network;

import commonnetwork.networking.data.PacketContext;
import me.kveex.somelocators.network.locator.CreateLocatorPointPayload;
import me.kveex.somelocators.network.locator.OpenLocatorScreenPayload;
import me.kveex.somelocators.network.locatorterminal.OpenCopyScreenPayload;
import me.kveex.somelocators.network.locatorterminal.OpenInspectScreenPayload;

import java.util.function.Consumer;

public class ClientPayloadHandler {
    public static Consumer<PacketContext<OpenInspectScreenPayload>> openInspectScreen = ctx -> {};
    public static Consumer<PacketContext<OpenCopyScreenPayload>> openCopyScreen = ctx -> {};
    public static Consumer<PacketContext<CreateLocatorPointPayload>> createLocatorPoint = ctx -> {};
    public static Consumer<PacketContext<OpenLocatorScreenPayload>> openLocatorScreen = ctx -> {};

    public static void handleOpenInspectScreen(PacketContext<OpenInspectScreenPayload> context) {
        openInspectScreen.accept(context);
    }

    public static void handleOpenCopyScreen(PacketContext<OpenCopyScreenPayload> context) {
        openCopyScreen.accept(context);
    }

    public static void handleCreateLocatorPoint(PacketContext<CreateLocatorPointPayload> context) {
        createLocatorPoint.accept(context);
    }

    public static void handleOpenLocatorScreen(PacketContext<OpenLocatorScreenPayload> context) {
        openLocatorScreen.accept(context);
    }
}
