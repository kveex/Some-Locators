package me.kveex.somelocators.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.kveex.somelocators.Constants;
import net.minecraft.client.KeyMapping;
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

    public static void init() {

    }
}
