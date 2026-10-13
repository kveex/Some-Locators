package me.kveex.somelocators.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.kveex.somelocators.Constants;
import me.kveex.somelocators.client.ui.screen.locator.LocatorMenuScreen;
import me.kveex.somelocators.client.ui.screen.locator.LocatorPointScreen;
import me.kveex.somelocators.client.ui.screen.locatorterminal.LocatorTerminalCopyScreen;
import me.kveex.somelocators.client.ui.screen.locatorterminal.LocatorTerminalInspectScreen;
import me.kveex.somelocators.component.LocatorComponent;
import me.kveex.somelocators.network.ClientPayloadHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class CommonClientClass {
    public static final KeyMapping.Category SOME_LOCATORS_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "category")
    );
    public static final KeyMapping openLocatorPointsMenu = new KeyMapping(
            "key.some_locators.open_locator_points_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            SOME_LOCATORS_CATEGORY
    );

    static {
        init();
    }

    public static void init() {
        ClientPayloadHandler.openInspectScreen = context ->
                Minecraft.getInstance().setScreen(new LocatorTerminalInspectScreen(context.message()));

        ClientPayloadHandler.openCopyScreen = context ->
                Minecraft.getInstance().setScreen(new LocatorTerminalCopyScreen(context.message()));

        ClientPayloadHandler.createLocatorPoint = context ->
                Minecraft.getInstance().setScreen(new LocatorPointScreen(context.message()));

        ClientPayloadHandler.openLocatorScreen = context -> {
            LocatorComponent locatorComponent = context.message().locatorComponent();
            if (locatorComponent.currentPoint().isEmpty()) return;
            Minecraft.getInstance().setScreen(new LocatorMenuScreen(
                    locatorComponent.currentPoint().get(),
                    locatorComponent.points(),
                    context.message().page()
            ));
        };
    }
}
