package me.kveex.somelocators.client.ui.util;

public record CoordsPair(int x, int y) {
    /**
     * Usually used if widget is square shaped
     * @param screenWidth Width of whole screen
     * @param screenHeight Height of whole screen
     * @param widgetSize Widget Width and height as one number
     * @return X and Y coordinates for passed widget dimensions to place it in center
     * @see CoordsPair#create(int, int, int, int)
     */
    public static CoordsPair create(int screenWidth, int screenHeight, int widgetSize) {
        return create(screenWidth, screenHeight, widgetSize, widgetSize);
    }

    /**
     * Creates coords pair that places widget in calculated for it center
     * @param screenWidth Width of whole screen
     * @param screenHeight Height of whole screen
     * @param widgetWidth Widget width
     * @param widgetHeight Widget height
     * @return X and Y coordinates for passed widget dimensions to place it in center
     */
    public static CoordsPair create(int screenWidth, int screenHeight, int widgetWidth, int widgetHeight) {
        int centerX = screenWidth / 2 - widgetWidth / 2;
        int centerY = screenHeight / 2 - widgetHeight / 2;

        return new CoordsPair(centerX, centerY);
    }

    public static CoordsPair createCentered(int startX, int startY, int screenWidth, int screenHeight, int widgetWidth, int widgetHeight) {
        int centerX = screenWidth / 2 - widgetWidth / 2;
        int centerY = screenHeight / 2 - widgetHeight / 2;

        return new CoordsPair(centerX + startX, centerY + startY);
    }

    public static CoordsPair createCentered(CoordsPair startPair, int screenWidth, int screenHeight, int widgetWidth, int widgetHeight) {
        return createCentered(startPair.x(), startPair.y(), screenWidth, screenHeight, widgetWidth, widgetHeight);
    }
}
