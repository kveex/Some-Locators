package me.kveex.somelocators;

import me.kveex.somelocators.client.CommonClientClass;
import me.kveex.somelocators.client.block.LocatorInspectorBlockEntityRenderer;
import me.kveex.somelocators.client.ui.util.BlockElementRenderState;
import me.kveex.somelocators.registry.ModBlockEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class SomeLocatorsClient {
    public SomeLocatorsClient(IEventBus bus) {
        bus.addListener(SomeLocatorsClient::registerBindings);
        bus.addListener(SomeLocatorsClient::registerEntityRenderers);
        bus.addListener(SomeLocatorsClient::registerPip);
    }

    @SubscribeEvent
    private static void registerBindings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CommonClientClass.SOME_LOCATORS_CATEGORY);
        event.register(CommonClientClass.openLocatorPointsMenu);
    }

    @SubscribeEvent
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntityTypes.LOCATOR_INSPECTOR_BLOCK_ENTITY.get(),
                LocatorInspectorBlockEntityRenderer::new
        );
    }

    @SubscribeEvent
    private static void registerPip(RegisterPictureInPictureRenderersEvent event) {
        event.register(
                BlockElementRenderState.class,
                BlockElementRenderState.Renderer::new
        );
    }
}
