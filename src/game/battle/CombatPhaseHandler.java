package game.battle;

import game.card.Card;
import game.card.CardElement;
import game.card.ElementalAdvantage;
import game.entity.PlayerEntity;
import game.message.BattleMessage;
import game.message.BattleMessageType;
import game.message.NewBattleMessageHandler;
import game.view.PlayerView;

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
                newBattleMessageHandler.addMessage(new BattleMessage(elementalMessage, BattleMessageType.ELEMENTAL_POWER_UP, 1, new PlayerView(firstPlayer)));

            } else if (elementalAdvantage < 0) {
                String elementalMessage = elemento1.name() + " é fraco contra " + elemento2.name() + ", -2 de poder para " + firstPlayerCard.getName() + ".";
                newBattleMessageHandler.addMessage(new BattleMessage(elementalMessage, BattleMessageType.ELEMENTAL_POWER_DOWN, 1, new PlayerView(firstPlayer)));
            }

            // Aplicamos os aprimoramentos das cartas em campo
            int firstPlayerTotalPower = firstPlayerCard.getTotalPower();
            int secondPlayerTotalPower = secondPlayerCard.getTotalPower();

            if (firstPlayerTotalPower == secondPlayerTotalPower) { // EMPATE
                String message = "Empate!";
                newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.BATTLE_DRAW, 1, new PlayerView(firstPlayer)));

            } else if (firstPlayerTotalPower > secondPlayerTotalPower) {
                secondPlayer.receiveDamage(firstPlayerCard.getDirectDamage());

                String winMessage = firstPlayer.getName() + " atacou com " + firstPlayerCard.getName() + " e venceu a rodada!";
                newBattleMessageHandler.addMessage(new BattleMessage(winMessage, BattleMessageType.BATTLE_WIN, 1, new PlayerView(firstPlayer)));

                String damageMessage = secondPlayer.getName() + " recebeu "+ firstPlayerCard.getDirectDamage() + " de dano.";
                newBattleMessageHandler.addMessage(new BattleMessage(damageMessage, BattleMessageType.DAMAGE, 2, new PlayerView(secondPlayer)));

            } else {
                firstPlayer.receiveDamage(secondPlayerCard.getDirectDamage());

                String winMessage = secondPlayer.getName() + " atacou com " + secondPlayerCard.getName() + " e venceu a rodada!";
                newBattleMessageHandler.addMessage(new BattleMessage(winMessage, BattleMessageType.BATTLE_WIN));

                String damageMessage = firstPlayer.getName() + " recebeu " + secondPlayerCard.getDirectDamage() +" de dano.";
                newBattleMessageHandler.addMessage(new BattleMessage(damageMessage, BattleMessageType.DAMAGE, 1, new PlayerView(firstPlayer)));
            }

            newBattleMessageHandler.addMessage(combatPhaseEndMessage);
            return;
        }

        if (bothPlayersDidntPlaceAnyCards(firstPlayerCard, secondPlayerCard)) {
            String message = "Nenhuma Carta foi colocada em campo. Nada acontece.";
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.BATTLE_PHASE_END, 1, new PlayerView(firstPlayer)));
            return;
        }

        if (firstPlayerCard == null) {
            firstPlayer.receiveDamage(secondPlayerCard.getDirectDamage());

            String noFieldCardMessage = firstPlayer.getName() + " não colocou nenhuma carta em campo.";
            String winMessage = secondPlayer.getName() + " atacou com " + secondPlayerCard.getName() + ".";
            String damageMessage = firstPlayer.getName() + " recebeu "+ secondPlayerCard.getDirectDamage() +" de dano.";

            newBattleMessageHandler.addMessage(new BattleMessage(noFieldCardMessage, BattleMessageType.SIMPLE));
            newBattleMessageHandler.addMessage(new BattleMessage(winMessage, BattleMessageType.BATTLE_WIN,2, new PlayerView(secondPlayer)));
            newBattleMessageHandler.addMessage(new BattleMessage(damageMessage, BattleMessageType.DAMAGE, 1, new PlayerView(firstPlayer)));
        }

        if (secondPlayerCard == null) {
            secondPlayer.receiveDamage(firstPlayerCard.getDirectDamage());

            String noFieldCardMessage = secondPlayer.getName() + " não colocou nenhuma carta em campo.";
            String winMessage = firstPlayer.getName() + " atacou com " + firstPlayerCard.getName() + ".";
            String damageMessage = secondPlayer.getName() + " recebeu "+ firstPlayerCard.getDirectDamage() + " de dano.";

            newBattleMessageHandler.addMessage(new BattleMessage(noFieldCardMessage, BattleMessageType.SIMPLE));
            newBattleMessageHandler.addMessage(new BattleMessage(winMessage, BattleMessageType.BATTLE_WIN, 1, new PlayerView(firstPlayer)));
            newBattleMessageHandler.addMessage(new BattleMessage(damageMessage, BattleMessageType.DAMAGE,2, new PlayerView(secondPlayer)));
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
