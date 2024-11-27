package game.cards;

public enum CardEffect {
    REDRAW, DRAW, BUFF, DEBUFF, HEAL;

    public static CardEffect fromString(String name) {
        for (CardEffect effect : CardEffect.values()) {
            if (effect.toString().equals(name)) {
                return effect;
            }
        }
        throw new IllegalArgumentException("Invalid card effect: " + name);
    }
}
