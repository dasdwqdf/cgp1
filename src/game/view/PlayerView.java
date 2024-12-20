package game.view;

import game.cards.Card;
import game.entity.PlayerEntity;

public class PlayerView {

    // Referência para o jogador
    PlayerEntity playerReference;

    // Atributos públicos para a UI do Jogador
    private final String name;
    private int hp;
    private int mana;
    private Card fieldCard;

    public PlayerView(PlayerEntity playerReference) {
        this.playerReference = playerReference;
        this.name = playerReference.getName();
        this.hp = playerReference.getHp();
        this.mana = playerReference.getMana();
        this.fieldCard = playerReference.getFieldCard() != null ? playerReference.getFieldCard().duplicate() : null;
    }

    public void updateHp() {
        this.hp = playerReference.getHp();
    }

    public void updateMana() {
        this.mana = playerReference.getMana();
    }

    public void updateFieldCard() {
        this.fieldCard = playerReference.getFieldCard() != null ? playerReference.getFieldCard().duplicate() : null;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMana() {
        return mana;
    }

    public Card getFieldCard() {
        return fieldCard;
    }
}
