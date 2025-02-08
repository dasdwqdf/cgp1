package game.card;

public enum CardEffect {
    REDRAW, DRAW, BUFF, HEAL, MANA;

    public static CardEffect fromString(String name) {
        for (CardEffect effect : CardEffect.values()) {
            if (effect.toString().equals(name)) {
                return effect;
            }
        }
        throw new IllegalArgumentException("Invalid card effect: " + name);
    }
}
