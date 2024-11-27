package game.cards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CardParser {

    public void loadDeckFile(CardManager cardManager, String fileName) {
        String deckPath = "decks/" + fileName;
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(deckPath);

        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Card newCard = parseStringToCard(line);
                    if (newCard != null) {
                        cardManager.addCardToDeck(newCard);
                    }
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Card parseStringToCard(String string) {
        String[] args = string.split(",");

        // Número de argumentos inválido
        if (args.length != 5) {
            return null;
        }

        int cardTypeValue = Integer.parseInt(args[0]);

        return switch (CardType.fromValue(cardTypeValue)) {
            case POWER -> parseStringToPowerCard(args);
            case EFFECT -> parseStringToEffectCard(args);
        };
    }

    public Card parseStringToEffectCard(String[] args) {
        String name = args[1];
        CardEffect cardEffect = CardEffect.fromString(args[2]);
        Integer effectArg = Integer.valueOf(args[3]);
        String description = args[4];

        return new Card(name, cardEffect, effectArg, description);
    }

    public Card parseStringToPowerCard(String[] args) {
        String name = args[1];
        CardElement cardElement = CardElement.fromString(args[2]);
        Integer power = Integer.valueOf(args[3]);
        String description = args[4];

        return new Card(name, cardElement, power, description);
    }
}
