package game.battle;

import game.cards.Card;
import game.entity.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class EffectHandler {

    BattleMessageHandler battleMessageHandler;

    public EffectHandler(BattleMessageHandler battleMessageHandler) {
        this.battleMessageHandler = battleMessageHandler;
    }

    public void handleEffect(PlayerEntity player, Card card) {
        switch (card.getCardEffect()) {
            case DRAW -> handleDrawEffect(player, card);
            case REDRAW -> handleRedrawEffect(player, card);
            case HEAL -> handleHealEffect(player, card);
            case BUFF -> handleBuffEffect(player, card, player.getFieldCard());
        };
    }

    private void handleBuffEffect(PlayerEntity player, Card card, Card playerFieldCard) {

        if (playerFieldCard == null) {
            battleMessageHandler.sendMessage("Você não possui cartas em campo para aplicar o buff.");
            return;
        }

        // Adicionamos o buff na lista
        playerFieldCard.setTempPower(card.getEffectArg());

        String[] messages = {
            player.getName() + " utilizou " + card.getName() + ".",
            "O poder do campo de " + player.getName() + " foi aumentado em " + card.getEffectArg() + "."
        };

        battleMessageHandler.sendMessage(messages);
    }

    private void handleHealEffect(PlayerEntity player, Card card) {
        int value = card.getEffectArg();
        player.heal(value);

        String[] messages = {
                player.getName() + " recuperou " + value + " ponto(s) de vida.",
        };

        battleMessageHandler.sendMessage(messages);
    }

    private void handleRedrawEffect(PlayerEntity player, Card card) {
        int numCards = card.getEffectArg();
        int numDiscardedCards = player.getCardManager().discardCards(numCards);
        int drawnCards = player.getCardManager().drawCards(numCards);

        String[] messages = {
                player.getName() + " utilizou " + card.getName(),
                numDiscardedCards + " carta(s) foram descartadas.",
                drawnCards + " carta(s) foram compradas."
        };

        battleMessageHandler.sendMessage(messages);
    }

    private void handleDrawEffect(PlayerEntity player, Card card) {
        int numCards = card.getEffectArg();
        int drawnCards = player.getCardManager().drawCards(numCards);

        String[] messages = {
                player.getName() + " utilizou " + card.getName(),
                drawnCards + " carta(s) foram compradas."
        };

        battleMessageHandler.sendMessage(messages);
    }
}
