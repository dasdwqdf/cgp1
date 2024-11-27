package game.entity;

import game.cards.Card;
import game.cards.CardManager;

import java.awt.image.BufferedImage;

public abstract class PlayerEntity {

    String name;
    int hp;
    CardManager cardManager;
    BufferedImage sprite;

    public PlayerEntity(String name, int hp, String deckName) {
        this.name = name;
        this.hp = hp;
        cardManager = new CardManager();
        cardManager.initDeck(deckName);
    }

    public void heal(int value) {
        hp += value;
    }

    public void receiveDamage(int value) {
        hp -= value;
    }

    public abstract Card selectBestCard();

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public CardManager getCardManager() {
        return cardManager;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", hp=" + hp +
                '}';
    }
}
