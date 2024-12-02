package game.battle;

import game.cards.Card;
import game.entity.PlayerEntity;

public class CombatPhaseHandler {

    BattleMessageHandler battleMessageHandler;
    EffectHandler effectHandler;

    public CombatPhaseHandler(BattleMessageHandler battleMessageHandler, EffectHandler effectHandler) {
        this.battleMessageHandler = battleMessageHandler;
        this.effectHandler = effectHandler;
    }

    public void handleCombatPhase(PlayerEntity firstPlayer, PlayerEntity secondPlayer) {
        Card firstPlayerCard = firstPlayer.getFieldCard();
        Card secondPlayerCard = secondPlayer.getFieldCard();

        // Se ambos os jogadores utilizaram cartas
        if (bothPlayersPlacedCards(firstPlayerCard, secondPlayerCard)) {

            // aplicamos os BUFFs/DEBUFFs
            int firstPlayerTotalPower = firstPlayerCard.getTotalPower();
            int secondPlayerTotalPower = secondPlayerCard.getTotalPower();

            // limpamos as listas os buff's temporários
            firstPlayerCard.clearTempPower();
            secondPlayerCard.clearTempPower();

            if (firstPlayerTotalPower == secondPlayerTotalPower) { // EMPATE
                battleMessageHandler.sendMessage("Empate!");

            } else if (firstPlayerTotalPower > secondPlayerTotalPower) {
                secondPlayer.receiveDamage(firstPlayerCard.getDirectDamage());

                String[] messages = {
                    firstPlayer.getName() + " atacou com " + firstPlayerCard.getName() + " e venceu a rodada!",
                    secondPlayer.getName() + " recebeu "+ firstPlayerCard.getDirectDamage() + " de dano."
                };

                battleMessageHandler.sendMessage(messages);

            } else {
                firstPlayer.receiveDamage(secondPlayerCard.getDirectDamage());

                String[] messages = {
                        secondPlayer.getName() + " atacou com " + secondPlayerCard.getName() + " e venceu a rodada!",
                        firstPlayer.getName() + " recebeu " + secondPlayerCard.getDirectDamage() +" de dano."
                };

                battleMessageHandler.sendMessage(messages);
            }

            return;
        }

        if (bothPlayersDidntPlaceAnyCards(firstPlayerCard, secondPlayerCard)) {
            battleMessageHandler.sendMessage("Nenhuma Carta foi colocada em campo. Nada acontece.");
            return;
        }

        if (firstPlayerCard == null) {
            firstPlayer.receiveDamage(secondPlayerCard.getDirectDamage());
            String[] messages = {
                    secondPlayer.getName() + " atacou com " + secondPlayerCard.getName(),
                    firstPlayer.getName() + " não colocou nenhuma carta em campo." ,
                    firstPlayer.getName() + " recebeu "+ secondPlayerCard.getDirectDamage() +" de dano."
            };
            battleMessageHandler.sendMessage(messages);
        }

        if (secondPlayerCard == null) {
            secondPlayer.receiveDamage(firstPlayerCard.getDirectDamage());
            String[] messages = {
                    firstPlayer.getName() + " atacou com " + firstPlayerCard.getName(),
                    secondPlayer.getName() + " não colocou nenhuma carta em campo." ,
                    secondPlayer.getName() + " recebeu "+ firstPlayerCard.getDirectDamage() + " de dano."
            };
            battleMessageHandler.sendMessage(messages);
        }
    }

    private boolean bothPlayersDidntPlaceAnyCards(Card firstPlayerCard, Card secondPlayerCard) {
        return firstPlayerCard == null && secondPlayerCard == null;
    }

    private boolean bothPlayersPlacedCards(Card firstPlayerCard, Card secondPlayerCard) {
        return firstPlayerCard != null && secondPlayerCard != null;
    }

}
