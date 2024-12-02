package game.entity;

import game.cards.Card;
import game.cards.CardManager;

import java.awt.image.BufferedImage;

public abstract class PlayerEntity {

    public static int maxMana = 3;

    String name;
    int hp;
    int mana;
    CardManager cardManager;
    BufferedImage sprite;

    public PlayerEntity(String name, int hp, int mana, String deckName) {
        this.mana = mana;
        this.name = name;
        this.hp = hp;
        cardManager = new CardManager();
        cardManager.initDeck(deckName);
    }

    public void regenMana(int value) {
        mana = Math.min(mana + value, maxMana);
    }

    public void consumeMana(int value) {
        mana -= value;
    }

    public void heal(int value) {
        hp += value;
    }

    public void receiveDamage(int value) {
        hp -= value;
    }

    public abstract Card selectBestCard();

    public abstract Card selectBestDiscardCard();

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

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
                "mana=" + mana + '\'' +
                ", name='" + name + '\'' +
                ", hp=" + hp +
                "name='" + name + '\'' +
                ", hp=" + hp +
                '}';
    }
}
