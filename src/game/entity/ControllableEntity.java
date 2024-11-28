package game.entity;

import game.cards.Card;

public class ControllableEntity extends PlayerEntity {
    public ControllableEntity(String name, int hp, int mana, String deckName) {
        super(name, hp, mana, deckName);
    }

    @Override
    public Card selectBestCard() {
        return null;
    }
}
