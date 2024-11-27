package game.battle;

import game.cards.Card;
import game.entity.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class EffectHandler {

    List<Card> effectsField1, effectsField2;
    BattleMessageHandler battleMessageHandler;

    public EffectHandler(BattleMessageHandler battleMessageHandler) {
        this.battleMessageHandler = battleMessageHandler;
        effectsField1 = new ArrayList<>();
        effectsField2 = new ArrayList<>();
    }

    public void handleEffect(PlayerEntity player, int currentPlayerIndex, Card card) {
        List<Card> playerEffectsField = currentPlayerIndex == 0 ? effectsField1 : effectsField2;
        List<Card> opponentEffectsField = currentPlayerIndex == 0 ? effectsField2 : effectsField1;

        switch (card.getCardEffect()) {
            case DRAW -> handleDrawEffect(player, card);
            case REDRAW -> handleRedrawEffect(player, card);
            case HEAL -> handleHealEffect(player, card);
            case BUFF -> handleBuffEffect(player, card, playerEffectsField);
            case DEBUFF -> handleDebuffEffect(player, card, opponentEffectsField);
        };
    }

    private void handleDebuffEffect(PlayerEntity player, Card card, List<Card> opponentEffects) {
        // Adicionamos o debuff na lista do oponente
        opponentEffects.add(card);

        String[] messages = {
                player.getName() + " utilizou " + card.getName() + ".",
                "O poder do campo do oponente foi reduzido em " + card.getEffectArg() + "."
        };

        battleMessageHandler.sendMessage(messages);
    }

    private void handleBuffEffect(PlayerEntity player, Card card, List<Card> playerEffects) {
        // Adicionamos o buff na lista
        playerEffects.add(card);

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

    public int getEffectValuesForPlayer(int playerIndex) {
        return playerIndex == 0 ? getEffectValues(effectsField1) : getEffectValues(effectsField2);
    }

    public int getEffectValues(List<Card> effects) {
        int value = 0;
        for (Card card : effects) {
            switch (card.getCardEffect()) {
                case BUFF -> value += card.getEffectArg();
                case DEBUFF -> value -= card.getEffectArg();
            }
        }

        return value;
    }

    public void clearEffects() {
        effectsField1.clear();
        effectsField2.clear();
    }
}
