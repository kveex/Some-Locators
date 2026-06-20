package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class LocatorRelatedScreen extends Screen {
    public static final Identifier LOCATOR_BACK_TEXTURE = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "textures/ui/locator_back.png");
    protected LocatorRelatedScreen() {
        super(Component.empty());
    }

    @Override
    public void renderBackground(@NonNull GuiGraphics graphics, int i, int j, float f) {
        int u = 0, v = 0;
        int textureSize = 384;
        var locatorTextureCenter = CoordsPair.create(this.width, this.height, textureSize);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED, LOCATOR_BACK_TEXTURE,
                locatorTextureCenter.x(), locatorTextureCenter.y(),
                u, v, textureSize, textureSize, textureSize, textureSize
        );

        this.renderTransparentBackground(graphics);
    }

    protected CoordsPair getLocatorCenter() {
        return new CoordsPair(this.width / 2, this.height / 2 - 24);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
