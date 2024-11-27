package game.entity;

import game.cards.Card;

public class ControllableEntity extends PlayerEntity {
    public ControllableEntity(String name, int hp, String deckName) {
        super(name, hp, deckName);
    }

    @Override
    public Card selectBestCard() {
        return null;
    }
}
