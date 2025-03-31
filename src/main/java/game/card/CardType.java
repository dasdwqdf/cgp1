package game.card;

public enum CardType {
    POWER,
    EFFECT;

    public static CardType fromValue(int value) {
        return switch (value) {
            case 0 -> POWER;
            case 1 -> EFFECT;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}

