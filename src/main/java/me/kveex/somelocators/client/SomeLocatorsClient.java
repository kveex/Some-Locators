package me.kveex.somelocators.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.block.LocatorInspectorBlockEntityRenderer;
import me.kveex.somelocators.client.ui.util.BlockElementRenderState;
import me.kveex.somelocators.registry.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class SomeLocatorsClient implements ClientModInitializer {
    public static final KeyMapping.Category SOME_LOCATORS_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "category")
    );

    public static final KeyMapping openLocatorPointsMenu = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.some_locators.open_locator_points_menu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_ALT,
                    SOME_LOCATORS_CATEGORY
            )
    );
    @Override
    public void onInitializeClient() {
        SpecialGuiElementRegistry.register(ctx -> new BlockElementRenderState.Renderer(ctx.vertexConsumers()));
        ModNetworkingClient.init();
        BlockEntityRenderers.register(ModBlockEntities.LOCATOR_INSPECTOR_BLOCK_ENTITY, LocatorInspectorBlockEntityRenderer::new);
    }
}
