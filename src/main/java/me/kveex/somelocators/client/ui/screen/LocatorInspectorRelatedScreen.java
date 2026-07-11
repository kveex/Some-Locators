package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.network.SetLocatorInspectorUnused;
import me.kveex.somelocators.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class LocatorInspectorRelatedScreen extends Screen {
    private CoordsPair uiStartCoords;
    private static final int uiWidth = 256, uiHeight = 192;
    private static final Identifier FRAME = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "locator_inspector_frame");
    private final BlockPos blockPos;
    private boolean blockMoved = false;

    protected LocatorInspectorRelatedScreen(BlockPos blockPos) {
        super(Component.empty());
        this.blockPos = blockPos;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        BlockState state = level.getBlockState(blockPos);
        if (!state.is(ModBlocks.LOCATOR_INSPECTOR)) {
            blockMoved = true;
            onClose();
        }
    }

    @Override
    protected void init() {
        this.uiStartCoords = CoordsPair.create(this.width, this.height, uiWidth, uiHeight);
    }

    @Override
    public void renderBackground(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(graphics);
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                FRAME,
                uiStartCoords.x(),
                uiStartCoords.y(),
                uiWidth,
                uiHeight
        );

    }

    @Override
    public void onClose() {
        SetLocatorInspectorUnused unused = new SetLocatorInspectorUnused(blockPos, blockMoved);
        unused.send();
        super.onClose();
    }

    public CoordsPair getUiStartCoords() {
        return this.uiStartCoords;
    }

    public int getUiWidth() {
        return uiWidth;
    }
}
