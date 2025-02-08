package game.view;

import game.card.Card;
import game.entity.PlayerEntity;

public class PlayerView {

    // Referência para o jogador
    PlayerEntity playerReference;

    // Atributos públicos para a UI do Jogador
    private final String name;
    private int hp;
    private int mana;
    private int nuCardsDeck;
    private Card fieldCard;

    public PlayerView(PlayerEntity playerReference) {
        this.playerReference = playerReference;
        this.name = playerReference.getName();
        this.hp = playerReference.getHp();
        this.mana = playerReference.getMana();
        this.nuCardsDeck = playerReference.getCardManager().getDeck().size();
        this.fieldCard = playerReference.getFieldCard() != null ? playerReference.getFieldCard().duplicate() : null;
    }

    public void updatePlayer(PlayerView playerView) {
        if (playerView == null) {
            return;
        }

        this.hp = playerView.getHp();
        this.mana = playerView.getMana();
        this.nuCardsDeck = playerView.getNuCardsDeck();
        this.fieldCard = playerView.getFieldCard();
    }

    public void updateHp() {
        this.hp = playerReference.getHp();
    }

    public void updateMana() {
        this.mana = playerReference.getMana();
    }

    public void updateNuCardsDeck() {
        this.nuCardsDeck = playerReference.getCardManager().getDeck().size();
    }

    public void updateFieldCard() {
        this.fieldCard = playerReference.getFieldCard() != null ? playerReference.getFieldCard().duplicate() : null;
        if (fieldCard != null) System.out.println(fieldCard);
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

    public int getNuCardsDeck() {
        return nuCardsDeck;
    }

    public Card getFieldCard() {
        return fieldCard;
    }
}
