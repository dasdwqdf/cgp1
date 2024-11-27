package game.entity;

import game.cards.Card;
import game.cards.CardType;

public class AiEntity extends PlayerEntity {

    public AiEntity(String name, int hp, String deckName) {
        super(name, hp, deckName);
    }

    @Override
    public Card selectBestCard() {
        Card bestCard = null;

        for (Card card : getCardManager().getHand()) {
            if (card.getCardType().equals(CardType.POWER)) {
                if (bestCard == null || card.getPower() > bestCard.getPower()) {
                    bestCard = card;
                }
            }
        }

        return bestCard;
    }
}
