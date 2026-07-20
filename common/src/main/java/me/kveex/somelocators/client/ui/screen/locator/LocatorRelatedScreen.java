package me.kveex.somelocators.client.ui.screen.locator;

import me.kveex.somelocators.Constants;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class LocatorRelatedScreen extends Screen {
    public static final Identifier LOCATOR_BACK_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/locator_back.png");
    private CoordsPair locatorCenter = null;
    protected LocatorRelatedScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        this.locatorCenter = new CoordsPair(this.width / 2, this.height / 2 - 24);
    }

    @Override
    public void renderBackground(@NonNull GuiGraphics graphics, int i, int j, float f) {
        int u = 0, v = 0;
        int textureSize = 384;
        var locatorTextureCenter = CoordsPair.centered(0, 0, this.width, this.height, textureSize, textureSize);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED, LOCATOR_BACK_TEXTURE,
                locatorTextureCenter.x(), locatorTextureCenter.y(),
                u, v, textureSize, textureSize, textureSize, textureSize
        );

        this.renderTransparentBackground(graphics);
    }

    protected CoordsPair getLocatorCenter() {
        return locatorCenter;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
