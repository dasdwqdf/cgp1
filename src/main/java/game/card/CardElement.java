package game.card;

public enum CardElement {
    FIRE,
    WATER,
    GRASS;

    public static CardElement fromString(String str) {
        for (CardElement e : CardElement.values()) {
            if (e.toString().equals(str)) {
                return e;
            }
        }

        throw new IllegalArgumentException("Invalid card element: " + str);
    }
}
