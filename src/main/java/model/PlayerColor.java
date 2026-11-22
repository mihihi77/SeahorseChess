package model;

public enum PlayerColor {
    RED,
    BLUE,
    GREEN,
    YELLOW;

    public static PlayerColor fromString(String value) {
        if (value == null) throw new IllegalArgumentException("Color cannot be null");
        return PlayerColor.valueOf(value.trim().toUpperCase());
    }
}