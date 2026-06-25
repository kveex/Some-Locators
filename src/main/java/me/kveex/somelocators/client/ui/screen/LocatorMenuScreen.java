package me.kveex.somelocators.client.ui.screen;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.ui.component.PageSwitchElement;
import me.kveex.somelocators.client.ui.component.PointInfoElement;
import me.kveex.somelocators.client.ui.util.CoordsPair;
import me.kveex.somelocators.component.PointComponent;

import java.util.List;

public class LocatorMenuScreen extends LocatorRelatedScreen {
    private PointComponent currentPoint;
    private final List<PointComponent> points;
    private int currentPage = 0;
    public static final int MAX_POINTS_ON_PAGE = 8;
    public static final int OFFSET_SMALL = 48;
    public static final int OFFSET_BIG = 72;
    private int pagesAmount;
    private PageSwitchElement pageSwitch;

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
        PointInfoElement.TooltipDrawer.clearHoveredWidget();
    }

    @Override
    protected void init() {
        super.init();

        this.pagesAmount = (int) Math.ceil((double) points.size() / MAX_POINTS_ON_PAGE);

        CoordsPair locatorCenter = this.getLocatorCenter();
        if (this.pageSwitch == null) {
            System.out.println("null");
            this.pageSwitch = new PageSwitchElement(locatorCenter.x() - 45, locatorCenter.y() + 80, this);
        }
        this.addRenderableWidget(this.pageSwitch);

        int startIndex = currentPage * MAX_POINTS_ON_PAGE;
        int endIndex = Math.min(startIndex + MAX_POINTS_ON_PAGE, points.size());
        int pointsOnPage = endIndex - startIndex;

        int upCount = Math.min(4, pointsOnPage);
        int downCount = pointsOnPage - upCount;

        for (int slot = upCount; slot >= 1; slot--) {
            int pointIndex = startIndex + slot - 1;
            PointComponent point = points.get(pointIndex);
            PointPlace pointPlace = PointPlace.of(slot);
            drawPoint(point, pointPlace);
        }

        for (int slot = 1; slot <= downCount; slot++) {
            int pointIndex = startIndex + upCount + slot - 1;
            PointComponent point = points.get(pointIndex);
            int pointPlaceIndex = upCount + slot;
            PointPlace pointPlace = PointPlace.of(pointPlaceIndex);
            drawPoint(point, pointPlace);
        }
    }

    public void removePoint(PointComponent point) {
        this.points.removeIf(p -> p.target().equals(point.target()));

        if (currentPoint != null && currentPoint.target().equals(point.target())) {
            currentPoint = points.isEmpty() ? null : points.getFirst();
        }

        if (this.points.isEmpty()) {
            this.onClose();
        }

        this.rebuildWidgets();
    }

    private void drawPoint(PointComponent point, PointPlace pointPlace) {
        var coords = getPointCoords(pointPlace);
        boolean isTracked = point.equals(this.currentPoint);
        PointInfoElement uiPointInfo = new PointInfoElement(point, isTracked, this, coords);
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

    public int getPagesAmount() {
        return this.pagesAmount;
    }

    public void setCurrentPage(int pageNumber) {
        int pageNum = Math.min(pageNumber, pagesAmount - 1);
        if (pageNumber < 0) pageNum = 0;
        this.currentPage = pageNum;
        SomeLocators.LOGGER.info("Raw page number: {} Final page number: {}", pageNumber, pageNum);
        this.rebuildWidgets();
    }
}
