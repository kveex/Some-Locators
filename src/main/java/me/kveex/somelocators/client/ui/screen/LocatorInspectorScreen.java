package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.component.PointInfoList;
import me.kveex.somelocators.client.ui.element.ItemElement;
import me.kveex.somelocators.client.ui.element.LabelElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.OpenLocatorInspectorMenuPayload;
import me.kveex.somelocators.network.SetLocatorInspectorUnused;
import me.kveex.somelocators.registry.ModBlocks;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import static me.kveex.somelocators.client.ui.screen.LocatorMenuScreen.MAX_POINTS_ON_PAGE;

public class LocatorInspectorScreen extends Screen {
    private static final Identifier FRAME = Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, "locator_inspector_frame");
    private final BlockPos blockPos;
    private final ItemStack firstLocator;
    private final ItemStack secondLocator;
    private boolean blockMoved = false;
    private static final int uiWidth = 256, uiHeight = 192;
    private CoordsPair uiStartCoords;

    public LocatorInspectorScreen(OpenLocatorInspectorMenuPayload payload) {
        super(Component.empty());
        blockPos = payload.pos();
        firstLocator = payload.firstLocator();
        secondLocator = payload.secondLocator();
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
        initInspectScreen();
    }

    private void initInspectScreen() {
        LodestonePointComponent component = firstLocator.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null) {
            Component text = Component.translatable("ui.some_locators.locator_inspector_inspect_error");
            int textWidth = this.getFont().width(text), textHeight = this.getFont().lineHeight;
            CoordsPair labelCenter = CoordsPair.createCentered(uiStartCoords, uiWidth, uiHeight, textWidth, textHeight);
            LabelElement errorLabel = new LabelElement(labelCenter.x(), labelCenter.y(), text);

            this.addRenderableWidget(errorLabel);
            return;
        }

        int pagesAmount = (int) Math.ceil((double) component.points().size() / MAX_POINTS_ON_PAGE);

        // Item
        int borderMargin = 4, itemScale = 3, itemSize = 16 * itemScale;
        int elementMargin = borderMargin * 3;
        int itemX = uiStartCoords.x() + borderMargin, itemY = uiStartCoords.y() + borderMargin;
        ItemElement item = new ItemElement(itemX, itemY, itemScale, firstLocator);

        this.addRenderableWidget(item);

        // Name Label
        Component nameText = firstLocator.getHoverName();
        int nameLabelX = uiStartCoords.x() + itemSize / 2, nameLabelY = uiStartCoords.y() + elementMargin;
        int maxLabelWidth = uiWidth - itemSize - borderMargin * itemScale;
        LabelElement nameLabel = new LabelElement(uiStartCoords.x(), uiStartCoords.y(), maxLabelWidth, nameText);
        CoordsPair labelCenter = CoordsPair.createCentered(nameLabelX, nameLabelY, uiWidth, 0, nameLabel.getWidth(), nameLabel.getHeight());
        nameLabel.setPosition(labelCenter.x(), labelCenter.y());

        this.addRenderableWidget(nameLabel);

        // Info Label
        Component infoText = Component.translatable(
                "ui.some_locators.locator_inspector_points_amount",
                component.points().size(),
                pagesAmount
        ).withStyle(ChatFormatting.DARK_GRAY);

        int infoLabelY = labelCenter.y() + elementMargin;
        LabelElement infoLabel = new LabelElement(labelCenter.x(), infoLabelY, maxLabelWidth, infoText);

        this.addRenderableWidget(infoLabel);

        // Current Point Label
        if (component.currentPoint().isPresent()) {
            Component currentText = Component.translatable(
                    "ui.some_locators.locator_inspector_current_name"
            ).withStyle(ChatFormatting.DARK_GRAY);

            Component currentName = Component.literal(
                    component.currentPoint().get().name()
            ).withStyle(ChatFormatting.DARK_GRAY);

            int currentLabelY = infoLabelY + elementMargin;
            LabelElement currentLabel = new LabelElement(labelCenter.x(), currentLabelY, maxLabelWidth, currentText, currentName);

            this.addRenderableWidget(currentLabel);
        }

        int listX = uiStartCoords.x() + borderMargin * 2, listY = uiStartCoords.y() + itemSize;
        int listWidth = uiWidth - borderMargin * 4, listHeight = 132 - borderMargin * 2 + elementMargin;

        PointInfoList list = new PointInfoList(
                listX, listY,
                listWidth, listHeight,
                component.points()
        );

        this.addRenderableWidget(list);
    }

    @Override
    public void onClose() {
        SetLocatorInspectorUnused unused = new SetLocatorInspectorUnused(blockPos, blockMoved);
        unused.send();
        super.onClose();
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
}
