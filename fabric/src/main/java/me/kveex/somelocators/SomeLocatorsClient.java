package me.kveex.somelocators;

import me.kveex.somelocators.client.CommonClientClass;
import me.kveex.somelocators.client.block.LocatorInspectorBlockEntityRenderer;
import me.kveex.somelocators.client.ui.util.BlockElementRenderState;
import me.kveex.somelocators.registry.ModBlockEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class SomeLocatorsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SpecialGuiElementRegistry.register(ctx -> new BlockElementRenderState.Renderer(ctx.vertexConsumers()));
        KeyBindingHelper.registerKeyBinding(CommonClientClass.openLocatorPointsMenu);
        BlockEntityRenderers.register(ModBlockEntityTypes.LOCATOR_INSPECTOR_BLOCK_ENTITY.get(), LocatorInspectorBlockEntityRenderer::new);
    }
}
