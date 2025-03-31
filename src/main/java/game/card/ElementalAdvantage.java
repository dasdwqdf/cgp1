package game.card;

public class ElementalAdvantage {

    public static int getAdvantage(CardElement element1, CardElement element2) {
        if (element1 == element2) return 0; // Caso neutro
        switch (element1) {
            case FIRE -> {
                return element2 == CardElement.GRASS ? +2 : -2;
            }

            case WATER -> {
                return element2 == CardElement.FIRE ? +2 : -2;
            }

            case GRASS -> {
                return element2 == CardElement.WATER ? +2 : -2;
            }

            default -> {
                return 0;
            }
        } // Casos especiais
    }

}
