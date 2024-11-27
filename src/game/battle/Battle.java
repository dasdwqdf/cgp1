package game.battle;

import game.cards.*;
import game.entity.AiEntity;
import game.entity.ControllableEntity;
import game.entity.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class Battle {

    BattleState battleState;

    List<PlayerEntity> players;

    int currentTurn;
    int currentPlayerIndex;
    Card[] fieldCards;
    List<Card> firstPlayerEffects, secondPlayerEffects;

    BattleMessageHandler battleMessageHandler;
    CombatPhaseHandler combatPhaseHandler;
    EffectHandler effectHandler;

    public Battle() {
        // Estado da Batalha
        battleState = BattleState.IN_PROGRESS;

        // Jogadores
        players = new ArrayList<>();
        players.add(new ControllableEntity("NOYUR", 6, "deck01.txt"));
        players.add(new AiEntity("BOT", 6, "deck01.txt"));

        // Outras variáveis
        this.currentTurn = 0;
        this.currentPlayerIndex = 0;
        this.fieldCards = new Card[2];
        this.firstPlayerEffects = new ArrayList<>();
        this.secondPlayerEffects = new ArrayList<>();
        this.battleMessageHandler = new BattleMessageHandler();
        this.effectHandler = new EffectHandler(battleMessageHandler);
        this.combatPhaseHandler = new CombatPhaseHandler(battleMessageHandler, effectHandler);
    }

    public void startBattle() {
        // Entidades
        PlayerEntity firstPlayer = players.get(0);
        PlayerEntity secondPlayer = players.get(1);

        // Embaralha o deck de ambos os jogadores
        // ambos compram 6 cartas
        for (PlayerEntity player : players) {
            player.getCardManager().shuffleDeck();
            player.getCardManager().drawCards(6);
        }

        // Mensagem de início de Batalha
        String[] messages = {
                "Batalha iniciada entre " + firstPlayer.getName() + " e " + secondPlayer.getName() + ".",
                "Ambos jogadores compraram 6 cartas."
        };

        battleMessageHandler.sendMessage(messages);
    }

    public boolean playCard(Card card) {
        // Pega o jogador atual
        PlayerEntity currentPlayer = players.get(currentPlayerIndex);
        CardManager cardManager = currentPlayer.getCardManager();

        switch (card.getCardType()) {
            case POWER:
                // Carta atual em campo
                Card currentPlayerFieldCard = fieldCards[currentPlayerIndex];

                // Caso já exista uma carta em campo para o jogador atual, retornamos
                if (currentPlayerFieldCard != null) {
                    if (currentPlayer instanceof ControllableEntity) {
                        battleMessageHandler.sendMessage("Não foi possível colocar " + card.getName() + ", pois " + currentPlayerFieldCard.getName() + " já está em campo.");
                    }
                    return false;
                }

                // Caso não exista uma carta em campo, consumimos a carta
                fieldCards[currentPlayerIndex] = card;
                cardManager.useCard(card);

                // Mensagem de carta em campo
                battleMessageHandler.sendMessage(currentPlayer.getName() + " colocou " + card.getName() + " em campo.");
                return true;

            case EFFECT:
                cardManager.useCard(card);
                effectHandler.handleEffect(currentPlayer, currentPlayerIndex, card);
                return true;

            default:
                return false;
        }
    }

    public void endTurn() {
        if (battleState.equals(BattleState.IN_PROGRESS)) {
            // Caso seja a finalização do turno do último jogador
            // devemos determinar a carta em campo vencedora
            if (bothPlayersCompletedRound()) {
                handleBattlePhase();
                updateBattleState();

                if (battleState.equals(BattleState.FINISHED)) {
                    return;
                }
            }

            // Incrementamos o turno e atualizamos o index do jogador
            currentTurn++;
            updateCurrentPlayerIndex();
            PlayerEntity currentPlayer = players.get(currentPlayerIndex);

            // Mensagem de fim de turno
            battleMessageHandler.sendMessage("Turno finalizado! Vez de " + currentPlayer.getName() + ".");

            // Jogador atual tenta comprar uma nova carta
            int drawnCards = currentPlayer.getCardManager().drawCards(1);
            if (drawnCards > 0) {
                battleMessageHandler.sendMessage(currentPlayer.getName() + " comprou " + drawnCards + " cartas(s).");
            }

            // Caso a entidade seja uma AI, tentamos selecionar a melhor carta
            Card card = currentPlayer.selectBestCard();
            boolean isPossibleToPlayCard = true;
            while (card != null && isPossibleToPlayCard) {
                isPossibleToPlayCard = playCard(card);
                card = currentPlayer.selectBestCard();
            }
        }
    }

    public void updateCurrentPlayerIndex() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void updateBattleState() {
        for (PlayerEntity player : players) {
            if (player.getHp() == 0) {
                battleState = BattleState.FINISHED;
                battleMessageHandler.sendMessage("Batalha finalizada!");
            }
        }
    }

    public void consumeFieldCards() {
        fieldCards[0] = null;
        fieldCards[1] = null;
    }

    public void handleBattlePhase() {
        // Jogadores
        PlayerEntity firstPlayer = players.get(0);
        PlayerEntity secondPlayer = players.get(1);

        // Pegamos as referencias das cartas de campo
        Card firstPlayerCard = fieldCards[0];
        Card secondPlayerCard = fieldCards[1];

        // Consumimos as cartas em campo
        consumeFieldCards();

        // Fase de combate para determina o vencedor da rodada
        combatPhaseHandler.handleCombatPhase(firstPlayer, secondPlayer, firstPlayerCard, secondPlayerCard, firstPlayerEffects, secondPlayerEffects);
    }

    public boolean bothPlayersCompletedRound() {
        System.out.println(currentPlayerIndex + 1 == players.size());
        return currentPlayerIndex + 1 == players.size();
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public BattleMessageHandler getBattleMessageHandler() {
        return battleMessageHandler;
    }

    public BattleState getBattleState() {
        return battleState;
    }
}
