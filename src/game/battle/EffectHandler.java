package game.battle;

import game.cards.Card;
import game.entity.AiEntity;
import game.entity.PlayerEntity;
import game.message.BattleMessage;
import game.message.BattleMessageType;
import game.message.NewBattleMessageHandler;

public class EffectHandler {

    NewBattleMessageHandler battleMessageHandler;

    public EffectHandler(NewBattleMessageHandler battleMessageHandler) {
        this.battleMessageHandler = battleMessageHandler;
    }

    public void handleEffect(PlayerEntity player, Card card) {
        // Mensagem de utilização da Carta de Efeito
        String cardMessage = player.getName() + " utilizou " + card.getName() + ".";
        int target = !(player instanceof AiEntity) ? 1 : 2;
        battleMessageHandler.addMessage(new BattleMessage(cardMessage, BattleMessageType.EFFECT_CARD, target));

        switch (card.getCardEffect()) {
            case DRAW -> handleDrawEffect(player, card, target);
            case REDRAW -> handleRedrawEffect(player, card, target);
            case HEAL -> handleHealEffect(player, card, target);
            case BUFF -> handleBuffEffect(player, card, player.getFieldCard(), target);
        };
    }

    private void handleBuffEffect(PlayerEntity player, Card card, Card playerFieldCard, int target) {

        if (playerFieldCard == null) {
            String message = "Você não possui cartas em campo para aplicar o buff.";
            battleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.SIMPLE));

            return;
        }

        // Aplicamos o aprimoramento temporário na carta
        playerFieldCard.addTempPower(card.getEffectArg());

        String powerUpMessage = "O poder do campo de " + player.getName() + " foi aumentado em " + card.getEffectArg() + ".";
        battleMessageHandler.addMessage(new BattleMessage(powerUpMessage, BattleMessageType.POWER_UP_EFFECT, target));
    }

    private void handleHealEffect(PlayerEntity player, Card card, int target) {
        int value = card.getEffectArg();
        player.heal(value);

        String healMessage = player.getName() + " recuperou " + value + " ponto(s) de vida.";
        battleMessageHandler.addMessage(new BattleMessage(healMessage, BattleMessageType.HEAL_EFFECT, target));
    }

    private void handleRedrawEffect(PlayerEntity player, Card card, int target) {
        int numCards = card.getEffectArg();
        int numDiscardedCards = player.getCardManager().discardCards(numCards);
        int drawnCards = player.getCardManager().drawCards(numCards);

        String redrawMessage = numDiscardedCards + " carta(s) foram descartadas e " + drawnCards + " carta(s) foram compradas.";
        battleMessageHandler.addMessage(new BattleMessage(redrawMessage, BattleMessageType.REDRAW_EFFECT, target));
    }

    private void handleDrawEffect(PlayerEntity player, Card card, int target) {
        int numCards = card.getEffectArg();
        int drawnCards = player.getCardManager().drawCards(numCards);

        String drawMessage = player.getName() + " comprou " + drawnCards + " carta(s).";
        battleMessageHandler.addMessage(new BattleMessage(drawMessage, BattleMessageType.DRAW_EFFECT, target));
    }
}
