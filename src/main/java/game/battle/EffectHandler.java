package game.battle;

import game.card.Card;
import game.entity.AiEntity;
import game.entity.PlayerEntity;
import game.message.BattleMessage;
import game.message.BattleMessageType;
import game.message.NewBattleMessageHandler;
import game.view.PlayerView;

public class EffectHandler {

    NewBattleMessageHandler battleMessageHandler;

    public EffectHandler(NewBattleMessageHandler battleMessageHandler) {
        this.battleMessageHandler = battleMessageHandler;
    }

    public boolean handleEffect(PlayerEntity player, Card card) {
        // Mensagem de utilização da Carta de Efeito
        String cardMessage = player.getName() + " utilizou " + card.getName() + ".";
        int target = !(player instanceof AiEntity) ? 1 : 2;

        boolean usedCard = switch (card.getCardEffect()) {
            case DRAW -> handleDrawEffect(player, card, target);
            case REDRAW -> handleRedrawEffect(player, card, target);
            case HEAL -> handleHealEffect(player, card, target);
            case BUFF -> handleBuffEffect(player, card, player.getFieldCard(), target);
            case MANA -> handleManaEffect(player, card, target);
        };

        if (usedCard) {
            player.consumeMana(card.getManaCost());
            battleMessageHandler.addMessage(new BattleMessage(cardMessage, BattleMessageType.EFFECT_CARD, target, new PlayerView(player)));
        }

        return usedCard;
    }

    private boolean handleManaEffect(PlayerEntity player, Card card, int target) {
        if (player.getMana() > 2) {
            String message = "Sua mana já está maximizada.";
            battleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.SIMPLE, target));

            return false;
        }

        // Regeneramos a mana do jogador
        player.regenMana(card.getEffectArg());

        String regenManaMessage = player.getName() + " regenerou " + card.getEffectArg() + " de mana.";
        battleMessageHandler.addMessage(new BattleMessage(regenManaMessage, BattleMessageType.REGEN_MANA, target, new PlayerView(player)));

        return true;
    }

    private boolean handleBuffEffect(PlayerEntity player, Card card, Card playerFieldCard, int target) {

        if (playerFieldCard == null) {
            String message = "Você não possui cartas em campo para aplicar o buff.";
            battleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.SIMPLE));

            return false;
        }

        // Aplicamos o aprimoramento temporário na carta
        playerFieldCard.addTempPower(card.getEffectArg());

        String powerUpMessage = "O poder do campo de " + player.getName() + " foi aumentado em " + card.getEffectArg() + ".";
        battleMessageHandler.addMessage(new BattleMessage(powerUpMessage, BattleMessageType.POWER_UP_EFFECT, target, new PlayerView(player)));

        return true;
    }

    private boolean handleHealEffect(PlayerEntity player, Card card, int target) {
        int value = card.getEffectArg();
        player.heal(value);

        String healMessage = player.getName() + " recuperou " + value + " ponto(s) de vida.";
        battleMessageHandler.addMessage(new BattleMessage(healMessage, BattleMessageType.HEAL_EFFECT, target, new PlayerView(player)));

        return true;
    }

    private boolean handleRedrawEffect(PlayerEntity player, Card card, int target) {
        int numCards = card.getEffectArg();
        int numDiscardedCards = player.getCardManager().discardCards(numCards);
        int drawnCards = player.getCardManager().drawCards(numCards);

        String redrawMessage = numDiscardedCards + " carta(s) foram descartadas e " + drawnCards + " carta(s) foram compradas.";
        battleMessageHandler.addMessage(new BattleMessage(redrawMessage, BattleMessageType.REDRAW_EFFECT, target, new PlayerView(player)));

        return true;
    }

    private boolean handleDrawEffect(PlayerEntity player, Card card, int target) {
        int numCards = card.getEffectArg();
        int drawnCards = player.getCardManager().drawCards(numCards);

        String drawMessage = player.getName() + " comprou " + drawnCards + " carta(s).";
        battleMessageHandler.addMessage(new BattleMessage(drawMessage, BattleMessageType.DRAW_EFFECT, target, new PlayerView(player)));

        return true;
    }
}
