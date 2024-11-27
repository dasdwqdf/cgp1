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
        players.add(new ControllableEntity("NOYUR", 6, 3, "deck01.txt"));
        players.add(new AiEntity("BOT", 6, 3,"deck01.txt"));

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

        // Embaralha o baralho de ambos os jogadores e
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

    public void startPlayerTurn() {
        // Resgatamos a entidade do Jogador
        PlayerEntity currentPlayer = players.get(currentPlayerIndex);
        CardManager cardManager = currentPlayer.getCardManager();

        // Tentamos comprar uma carta
        int drawnCards = cardManager.drawCards(1);

        // Mensagem de compra de cartas
        battleMessageHandler.sendMessage(currentPlayer.getName() + " comprou " + drawnCards + " cartas(s) do baralho.");
    }

    private void startAiTurn() {
        // Resgatamos a entidade da AI
        PlayerEntity aiEntity = players.get(currentPlayerIndex);
        CardManager cardManager = aiEntity.getCardManager();

        // Tentamos comprar uma carta, assim como no turno do jogador
        int drawnCards = cardManager.drawCards(1);

        // Mensagem de compra de cartas
        if (drawnCards > 0) {
            battleMessageHandler.sendMessage(aiEntity.getName() + " comprou " + drawnCards + " cartas(s) do baralho.");
        }

        // AI seleciona o melhor movimento
        Card card = aiEntity.selectBestCard();
        boolean isCardPlayable = true;

        // Enquanto a AI tiver opções viáveis de acordo com o algoritmo,
        // ela utiliza as cartas selecionadas
        while(card != null && isCardPlayable) {
            isCardPlayable = playCard(card);
            card = aiEntity.selectBestCard();
        }

        // Finalizamos o turno da AI
        endAiTurn();
    }

    public void endPlayerTurn() {
        if (battleState.equals(BattleState.IN_PROGRESS)) {
            // Incrementamos o turno e atualizamos o index do jogador
            currentTurn++;
            updateCurrentPlayerIndex();

            // Iniciamos o turno da AI
            startAiTurn();
        }
    }

    public void endAiTurn() {
        // Como a AI sempre é a última a jogar, garantimos a finalização da rodada
        // Assim, lidamos com a questão da batalha assim que a AI finaliza seu turno
        handleBattlePhase();

        // Atualizamos o estado da batalha
        updateBattleState();

        if (battleState.equals(BattleState.IN_PROGRESS)) {
            // Incrementamos o turno e atualizamos o index do jogador atual
            currentTurn++;
            updateCurrentPlayerIndex();

            // Iniciamos o turno do jogador
            startPlayerTurn();
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

        // Fase de combate para determinar o vencedor da rodada
        combatPhaseHandler.handleCombatPhase(firstPlayer, secondPlayer, firstPlayerCard, secondPlayerCard);
    }

    public boolean bothPlayersCompletedRound() {
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
