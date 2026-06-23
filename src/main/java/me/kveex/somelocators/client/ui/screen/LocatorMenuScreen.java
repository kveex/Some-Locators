package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.component.UiPointInfo;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.PointComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

public class LocatorMenuScreen extends LocatorRelatedScreen {
    private PointComponent currentPoint;
    private final List<PointComponent> points;
    private int currentPage = 0;
    public static final int MAX_POINTS_ON_PAGE = 8;
    public static final int OFFSET_SMALL = 48;
    public static final int OFFSET_BIG = 72;

    public LocatorMenuScreen(PointComponent currentPoint, List<PointComponent> points) {
        this.currentPoint = currentPoint;
        this.points = points;
    }

    public void setCurrentPoint(PointComponent currentPoint) {
        this.currentPoint = currentPoint;
        this.rebuildWidgets();
    }

    @Override
    public void onClose() {
        super.onClose();
        UiPointInfo.TooltipDrawer.clearHoveredWidget();
    }

    @Override
    protected void init() {
        super.init();

        int pages = (int) Math.ceil((double) points.size() / MAX_POINTS_ON_PAGE);

        if (currentPage > 0) {
            Button previousButton = Button.builder(Component.literal("previous"), button -> {
                        currentPage--;
                        this.rebuildWidgets();
                    })
                    .pos(300, this.height - 24)
                    .size(120, 20)
                    .build();
            this.addRenderableWidget(previousButton);
        }

        if (currentPage < pages - 1) {
            Button nextButton = Button.builder(Component.literal("next"), button -> {
                        currentPage++;
                        this.rebuildWidgets();
                    })
                    .pos(200, this.height - 24)
                    .size(120, 20)
                    .build();
            this.addRenderableWidget(nextButton);
        }


        int startIndex = currentPage * MAX_POINTS_ON_PAGE;
        int endIndex = Math.min(startIndex + MAX_POINTS_ON_PAGE, points.size());
        int pointsOnPage = endIndex - startIndex;

        int upCount = Math.min(4, pointsOnPage);
        int downCount = pointsOnPage - upCount;

        for (int slot = upCount; slot >= 1; slot--) {
            int pointIndex = startIndex + slot - 1;
            PointComponent point = points.get(pointIndex);
            SomeLocators.LOGGER.info("Page: {} UpSlot: {}", currentPage, slot);
            PointPlace pointPlace = PointPlace.of(slot);
            drawPoint(point, pointPlace);
        }

        for (int slot = 1; slot <= downCount; slot++) {
            int pointIndex = startIndex + upCount + slot - 1;
            PointComponent point = points.get(pointIndex);
            int pointPlaceIndex = upCount + slot;
            SomeLocators.LOGGER.info("Page: {} DownSlot: {}", currentPage, pointPlaceIndex);
            PointPlace pointPlace = PointPlace.of(pointPlaceIndex);
            drawPoint(point, pointPlace);
        }
    }

    private void drawPoint(PointComponent point, PointPlace pointPlace) {
        var coords = getPointCoords(pointPlace);
        boolean isTracked = point.equals(this.currentPoint);
        UiPointInfo uiPointInfo = new UiPointInfo(point, isTracked, this, coords);
        this.addRenderableWidget(uiPointInfo);
    }

    private CoordsPair getPointCoords(PointPlace pointPlace) {
        var center = this.getLocatorCenter();
        return switch (pointPlace) {
            case FIRST -> new CoordsPair(center.x() - OFFSET_BIG - 24, center.y() - OFFSET_SMALL);
            case SECOND -> new CoordsPair(center.x() - OFFSET_SMALL, center.y() - OFFSET_BIG);
            case THIRD -> new CoordsPair(center.x() + OFFSET_SMALL / 2, center.y() - OFFSET_BIG);
            case FOURTH -> new CoordsPair(center.x() + OFFSET_BIG, center.y() - OFFSET_SMALL);
            case FIFTH -> new CoordsPair(center.x() + OFFSET_BIG, center.y() + OFFSET_SMALL / 2);
            case SIXTH -> new CoordsPair(center.x() + OFFSET_SMALL / 2, center.y() + OFFSET_BIG - 24);
            case SEVENTH -> new CoordsPair(center.x() - OFFSET_SMALL, center.y() + OFFSET_BIG - 24);
            case EIGHTH -> new CoordsPair(center.x() - OFFSET_BIG - 24, center.y() + OFFSET_SMALL - 24);
            case MISS -> new CoordsPair(center.x(), center.y());
        };
    }

    private enum PointPlace {
        FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH, MISS;

        public static PointPlace of(int value) {
            PointPlace[] values = values();
            return (value >= 1 && value <= values.length) ? values[value - 1] : MISS;
        }
    }
}
