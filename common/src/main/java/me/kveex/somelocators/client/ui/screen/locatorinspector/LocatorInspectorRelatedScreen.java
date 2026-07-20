package me.kveex.somelocators.client.ui.screen.locatorinspector;

import commonnetwork.api.Dispatcher;
import me.kveex.somelocators.Constants;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.network.locatorinspector.SetUnusedPayload;
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
    private static final int TOP_BORDER = 12;
    public static final int uiWidth = 256, uiHeight = 192 + TOP_BORDER;
    private static final Identifier FRAME = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "locator_inspector_frame");
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
        if (!state.is(ModBlocks.LOCATOR_INSPECTOR.get())) {
            blockMoved = true;
            onClose();
        }
    }

    @Override
    protected void init() {
        this.uiStartCoords = CoordsPair.centered(0, 0, this.width, this.height, uiWidth, uiHeight);
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
        SetUnusedPayload unused = new SetUnusedPayload(blockPos, blockMoved);
        Dispatcher.sendToServer(unused);
        super.onClose();
    }

    public CoordsPair getUiStartCoords() {
        return new CoordsPair(uiStartCoords.x(), uiStartCoords.y() + TOP_BORDER / 2);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }
}
