package me.kveex.somelocators.client.ui.util;

public record CoordsPair(int x, int y) {
    public static CoordsPair centered(int containerX, int containerY, int containerWidth, int containerHeight, int widgetWidth, int widgetHeight) {
        return new CoordsPair(
                containerX + containerWidth  / 2 - widgetWidth  / 2,
                containerY + containerHeight / 2 - widgetHeight / 2
        );
    }

    public static int centeredX(int containerX, int containerWidth, int widgetWidth) {
        return containerX + containerWidth / 2 - widgetWidth / 2;
    }
}