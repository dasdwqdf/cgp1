package ui.enums;

public enum PlayerStatusPosition {
    TOP_LEFT,
    TOP_RIGHT;

    public int getX(int screenWidth, int textWidth) {
        return switch (this) {
            case TOP_LEFT -> 8;
            case TOP_RIGHT -> screenWidth - textWidth - 8;
        };
    }

    public int getY() {
        return switch (this) {
            case TOP_LEFT, TOP_RIGHT -> 32;
        };
    }
}
