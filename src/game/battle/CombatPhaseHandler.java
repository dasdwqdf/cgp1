package game.battle;

import game.cards.Card;
import game.cards.CardEffect;
import game.entity.PlayerEntity;

import java.sql.SQLOutput;
import java.util.List;

public class CombatPhaseHandler {

    BattleMessageHandler battleMessageHandler;
    EffectHandler effectHandler;

    public CombatPhaseHandler(BattleMessageHandler battleMessageHandler, EffectHandler effectHandler) {
        this.battleMessageHandler = battleMessageHandler;
        this.effectHandler = effectHandler;
    }

    public void handleCombatPhase(PlayerEntity firstPlayer, PlayerEntity secondPlayer, Card firstPlayerCard, Card secondPlayerCard, List<Card> firstPlayerEffects, List<Card> secondPlayerEffects) {
        // Se ambos os jogadores utilizaram cartas
        if (bothPlayersPlacedCards(firstPlayerCard, secondPlayerCard)) {
            int firstPlayerRawPower = firstPlayerCard.getPower();
            int secondPlayerRawPower = secondPlayerCard.getPower();

            // Aplicamos os BUFFs/DEBUFFs
            int firstPlayerTotalPower = firstPlayerRawPower + effectHandler.getEffectValuesForPlayer(0);
            int secondPlayerTotalPower = secondPlayerRawPower + effectHandler.getEffectValuesForPlayer(1);

            // Limpamos as listas de efeitos
            effectHandler.clearEffects();

            System.out.println("Jogador 1: " + firstPlayerTotalPower + "\nJogador 2: " + secondPlayerTotalPower);

            if (firstPlayerTotalPower == secondPlayerTotalPower) { // EMPATE
                battleMessageHandler.sendMessage("Empate!");

            } else if (firstPlayerTotalPower > secondPlayerTotalPower) {
                secondPlayer.receiveDamage(1);

                String[] messages = {
                    firstPlayer.getName() + " atacou com " + firstPlayerCard.getName() + " e venceu a rodada!",
                    secondPlayer.getName() + " recebeu 1 de dano."
                };

                battleMessageHandler.sendMessage(messages);

            } else {
                firstPlayer.receiveDamage(1);

                String[] messages = {
                        secondPlayer.getName() + " atacou com " + secondPlayerCard.getName() + " e venceu a rodada!",
                        firstPlayer.getName() + " recebeu 1 de dano."
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
            firstPlayer.receiveDamage(1);
            String[] messages = {
                    secondPlayer.getName() + " atacou com " + secondPlayerCard.getName(),
                    firstPlayer.getName() + " não colocou nenhuma carta em campo." ,
                    firstPlayer.getName() + " recebeu 1 de dano."
            };
            battleMessageHandler.sendMessage(messages);
        }

        if (secondPlayerCard == null) {
            secondPlayer.receiveDamage(1);
            String[] messages = {
                    firstPlayer.getName() + " atacou com " + firstPlayerCard.getName(),
                    secondPlayer.getName() + " não colocou nenhuma carta em campo." ,
                    secondPlayer.getName() + " recebeu 1 de dano."
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
