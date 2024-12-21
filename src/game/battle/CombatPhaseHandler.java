package game.battle;

import game.cards.Card;
import game.cards.CardElement;
import game.cards.ElementalAdvantage;
import game.entity.PlayerEntity;
import game.message.BattleMessage;
import game.message.BattleMessageType;
import game.message.NewBattleMessageHandler;

public class CombatPhaseHandler {

    NewBattleMessageHandler newBattleMessageHandler;
    EffectHandler effectHandler;

    public CombatPhaseHandler(NewBattleMessageHandler newBattleMessageHandler, EffectHandler effectHandler) {
        this.newBattleMessageHandler = newBattleMessageHandler;
        this.effectHandler = effectHandler;
    }

    public void handleCombatPhase(PlayerEntity firstPlayer, PlayerEntity secondPlayer) {
        Card firstPlayerCard = firstPlayer.getFieldCard();
        Card secondPlayerCard = secondPlayer.getFieldCard();

        BattleMessage combatPhaseEndMessage = new BattleMessage("Fase de Batalha encerrada!", BattleMessageType.BATTLE_PHASE_END);

        // Se ambos os jogadores utilizaram cartas
        if (bothPlayersPlacedCards(firstPlayerCard, secondPlayerCard)) {
            CardElement elemento1 = firstPlayerCard.getCardElement();
            CardElement elemento2 = secondPlayerCard.getCardElement();

            // Aplicamos as vantagens/desvantagens elementais
            int elementalAdvantage = ElementalAdvantage.getAdvantage(elemento1, elemento2);
            firstPlayerCard.addTempPower(elementalAdvantage);

            // Mensagens para caso seja aplicado alguma vantagem/desvantagem
            if (elementalAdvantage > 0) {
                String elementalMessage = elemento1.name() + " é efetivo contra " + elemento2.name() + ", +2 de poder para " + firstPlayerCard.getName() + ".";
                newBattleMessageHandler.addMessage(new BattleMessage(elementalMessage, BattleMessageType.ELEMENTAL_POWER_UP));

            } else if (elementalAdvantage < 0) {
                String elementalMessage = elemento1.name() + " é fraco contra " + elemento2.name() + ", -2 de poder para " + firstPlayerCard.getName() + ".";
                newBattleMessageHandler.addMessage(new BattleMessage(elementalMessage, BattleMessageType.ELEMENTAL_POWER_DOWN));
            }

            // Aplicamos os aprimoramentos das cartas em campo
            int firstPlayerTotalPower = firstPlayerCard.getTotalPower();
            int secondPlayerTotalPower = secondPlayerCard.getTotalPower();

            if (firstPlayerTotalPower == secondPlayerTotalPower) { // EMPATE
                String message = "Empate!";
                newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.BATTLE_DRAW));

            } else if (firstPlayerTotalPower > secondPlayerTotalPower) {
                secondPlayer.receiveDamage(firstPlayerCard.getDirectDamage());

                String winMessage = firstPlayer.getName() + " atacou com " + firstPlayerCard.getName() + " e venceu a rodada!";
                newBattleMessageHandler.addMessage(new BattleMessage(winMessage, BattleMessageType.BATTLE_WIN));

                String damageMessage = secondPlayer.getName() + " recebeu "+ firstPlayerCard.getDirectDamage() + " de dano.";
                newBattleMessageHandler.addMessage(new BattleMessage(damageMessage, BattleMessageType.DAMAGE, 2));

            } else {
                firstPlayer.receiveDamage(secondPlayerCard.getDirectDamage());

                String winMessage = secondPlayer.getName() + " atacou com " + secondPlayerCard.getName() + " e venceu a rodada!";
                newBattleMessageHandler.addMessage(new BattleMessage(winMessage, BattleMessageType.BATTLE_WIN, 2));

                String damageMessage = firstPlayer.getName() + " recebeu " + secondPlayerCard.getDirectDamage() +" de dano.";
                newBattleMessageHandler.addMessage(new BattleMessage(damageMessage, BattleMessageType.DAMAGE));
            }

            newBattleMessageHandler.addMessage(combatPhaseEndMessage);
            return;
        }

        if (bothPlayersDidntPlaceAnyCards(firstPlayerCard, secondPlayerCard)) {
            String message = "Nenhuma Carta foi colocada em campo. Nada acontece.";
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.BATTLE_PHASE_END));
            return;
        }

        if (firstPlayerCard == null) {
            firstPlayer.receiveDamage(secondPlayerCard.getDirectDamage());

            String noFieldCardMessage = firstPlayer.getName() + " não colocou nenhuma carta em campo.";
            String winMessage = secondPlayer.getName() + " atacou com " + secondPlayerCard.getName() + ".";
            String damageMessage = firstPlayer.getName() + " recebeu "+ secondPlayerCard.getDirectDamage() +" de dano.";

            newBattleMessageHandler.addMessage(new BattleMessage(noFieldCardMessage, BattleMessageType.SIMPLE));
            newBattleMessageHandler.addMessage(new BattleMessage(winMessage, BattleMessageType.BATTLE_WIN,2));
            newBattleMessageHandler.addMessage(new BattleMessage(damageMessage, BattleMessageType.DAMAGE));
        }

        if (secondPlayerCard == null) {
            secondPlayer.receiveDamage(firstPlayerCard.getDirectDamage());

            String noFieldCardMessage = secondPlayer.getName() + " não colocou nenhuma carta em campo.";
            String winMessage = firstPlayer.getName() + " atacou com " + firstPlayerCard.getName() + ".";
            String damageMessage = secondPlayer.getName() + " recebeu "+ firstPlayerCard.getDirectDamage() + " de dano.";

            newBattleMessageHandler.addMessage(new BattleMessage(noFieldCardMessage, BattleMessageType.SIMPLE));
            newBattleMessageHandler.addMessage(new BattleMessage(winMessage, BattleMessageType.BATTLE_WIN));
            newBattleMessageHandler.addMessage(new BattleMessage(damageMessage, BattleMessageType.DAMAGE,2));
        }

        newBattleMessageHandler.addMessage(combatPhaseEndMessage);
    }

    private boolean bothPlayersDidntPlaceAnyCards(Card firstPlayerCard, Card secondPlayerCard) {
        return firstPlayerCard == null && secondPlayerCard == null;
    }

    private boolean bothPlayersPlacedCards(Card firstPlayerCard, Card secondPlayerCard) {
        return firstPlayerCard != null && secondPlayerCard != null;
    }

}
