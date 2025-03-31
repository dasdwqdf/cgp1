package game.battle;

import game.battle.statistic.BattleStatistics;
import game.card.*;
import game.entity.AiEntity;
import game.entity.ControllableEntity;
import game.entity.PlayerEntity;
import game.message.BattleMessage;
import game.message.BattleMessageType;
import game.message.NewBattleMessageHandler;
import game.view.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class Battle {

    BattleState battleState;
    List<PlayerEntity> players;

    int currentTurn;
    int currentPlayerIndex;

    NewBattleMessageHandler newBattleMessageHandler;
    CombatPhaseHandler combatPhaseHandler;
    EffectHandler effectHandler;

    // Batalha entre IAs
    public Battle(boolean vsAI) {
        // Batalha muda para estado em progresso
        battleState = BattleState.IN_PROGRESS;

        // Inicialização das IAs
        initAis();

        // Inicialização de alguns parâmetros
        this.currentTurn = 0;
        this.currentPlayerIndex = 0;
        this.newBattleMessageHandler = new NewBattleMessageHandler();
        this.effectHandler = new EffectHandler(newBattleMessageHandler);
        this.combatPhaseHandler = new CombatPhaseHandler(newBattleMessageHandler, effectHandler);
    }

    // Batalha Comum entre jogador x IA
    public Battle() {
        // Batalha muda para estado em progresso
        battleState = BattleState.IN_PROGRESS;

        // Inicialização dos Jogadores
        initPlayers();

        // Inicialização de alguns parâmetros
        this.currentTurn = 0;
        this.currentPlayerIndex = 0;
        this.newBattleMessageHandler = new NewBattleMessageHandler();
        this.effectHandler = new EffectHandler(newBattleMessageHandler);
        this.combatPhaseHandler = new CombatPhaseHandler(newBattleMessageHandler, effectHandler);
    }

    public void initPlayers() {
        players = new ArrayList<>();
        players.add(new ControllableEntity("JOGADOR", 6, 0, "deck01.txt"));
        players.add(new AiEntity("BOT", 6, 0,"deck01.txt"));
    }

    public void initAis() {
        players = new ArrayList<>();
        players.add(new AiEntity("IA Focada na Maximização de Poder", 6, 0, "deck01.txt", AiEntity.AiEntityType.AI_TYPE_1));
        players.add(new AiEntity("IA Focada na Conservação de Mana", 6, 0,"deck01.txt", AiEntity.AiEntityType.AI_TYPE_2));
    }
    
    public BattleStatistics startAiBattle() {
        // AIs
        AiEntity firstAi = (AiEntity) players.get(0);
        AiEntity secondAi = (AiEntity) players.get(1);

        // Medidas
        int firstAiTotalPower = 0;
        int secondAiTotalPower = 0;
        int firstAiUnusedMana = 0;
        int secondAiUnusedMana = 0;
        int firstAiNuMovements = 0;
        int secondAiNuMovements = 0;
        int nuTurns = 0;

        // Embaralhamos o baralho de ambos os jogadores e compramos 5 cartas
        for (PlayerEntity player : players) {
            player.getCardManager().shuffleDeck();
            player.getCardManager().drawCards(5);
        }
        
        while (BattleState.IN_PROGRESS == battleState) {
            // Turno da primeira AI
            handleTurnStart(firstAi);
            firstAiNuMovements += handleAiTurn(firstAi);
            firstAiUnusedMana += firstAi.getMana(); // Mana restante após o turno
            firstAiTotalPower += firstAi.getFieldCard() != null ? firstAi.getFieldCard().getTotalPower() : 0; // Função para calcular o dano causado
            updateCurrentPlayerIndex();

            // Turno da segunda AI
            handleTurnStart(secondAi);
            secondAiNuMovements += handleAiTurn(secondAi);
            secondAiUnusedMana += secondAi.getMana(); // Mana restante após o turno
            secondAiTotalPower += secondAi.getFieldCard() != null ? secondAi.getFieldCard().getTotalPower() : 0;
            updateCurrentPlayerIndex();

            // Fase de combate
            handleBattlePhase();

            nuTurns++;

            // Atualizamos o estado da batalha
            updateBattleState();
        }

        String winner = firstAi.getHp() > 0 ? firstAi.getName() : secondAi.getName();

        return new BattleStatistics(nuTurns, firstAiTotalPower, secondAiTotalPower,
                firstAiNuMovements, secondAiNuMovements, firstAiUnusedMana, secondAiUnusedMana, winner);
    }

    public void startBattle() {
        // Entidades
        PlayerEntity firstPlayer = players.get(0);
        PlayerEntity secondPlayer = players.get(1);

        // Embaralhamos o baralho de ambos os jogadores e compramos 5 cartas
        for (PlayerEntity player : players) {
            player.getCardManager().shuffleDeck();
            player.getCardManager().drawCards(5);
        }

        // Mensagem de Início de Batalha
        String message = "Batalha iniciada entre " + firstPlayer.getName() + " e " + secondPlayer.getName() + ".";
        newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.DRAW_CARD, 1, new PlayerView(firstPlayer)));

        // Inicia o Turno do Jogador
        startPlayerTurn();
    }

    public void handleTurnStart(PlayerEntity currentPlayer) {
        CardManager cardManager = currentPlayer.getCardManager();
        int target = players.indexOf(currentPlayer) == 0 ? 1 : 2;

        // Jogador recupera mana
        if (currentPlayer.getMana() < PlayerEntity.maxMana) {
            currentPlayer.regenMana(1);
            String message = currentPlayer.getName() + " recuperou 1 de mana.";
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.REGEN_MANA, target, new PlayerView(currentPlayer)));
        }

        // Tentamos comprar uma carta
        int drawnCards = cardManager.drawCards(1);

        if (drawnCards > 0) {
            // Mensagem de compra de cartas
            String message = currentPlayer.getName() + " comprou " + drawnCards + " cartas(s) do baralho.";
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.DRAW_CARD, target, new PlayerView(currentPlayer)));
        }

        // Lidamos com o descarte de cartas extras
        handleDiscard(currentPlayer);
    }

    public void handleDiscard(PlayerEntity currentPlayer) {
        // Gerenciador de Cartas do Jogador atual
        CardManager currentPlayerCardManager = currentPlayer.getCardManager();

        int handSize = currentPlayerCardManager.getHand().size();

        // Verificamos se o número de cartas do jogador excede o tamanho da mão
        if (handSize > CardManager.handMaxSize) {

            if (currentPlayer instanceof AiEntity) {
                while (handSize > CardManager.handMaxSize) {
                    Card discardCard = currentPlayer.selectBestDiscardCard();
                    discardCard(discardCard);
                    handSize = currentPlayerCardManager.getHand().size();
                }

            } else {
                newBattleMessageHandler.addMessage(new BattleMessage("Sua mão está cheia, selecione uma carta para descartar.", BattleMessageType.DISCARD_CARD, 1, new PlayerView(currentPlayer)));
            }
        }

    }

    public void startPlayerTurn() {
        // Resgatamos a entidade do Jogador
        PlayerEntity currentPlayer = getPlayerEntities().get(currentPlayerIndex);

        // Fazemos o processamento genérico de turno de um objeto PlayerEntity
        handleTurnStart(currentPlayer);
    }

    private void startAiTurn() {
        // Resgatamos a entidade da AI
        AiEntity aiEntity = (AiEntity) players.get(currentPlayerIndex);

        // Processamento genérico de turno de um PlayerEntity
        handleTurnStart(aiEntity);
        handleAiTurn(aiEntity);
        endAiTurn();
    }

    private int handleAiTurn(AiEntity aiEntity) {
        List<Card> aiMovements = aiEntity.selectMovementsAi();
        int nuMovements = 0;

        while ((aiMovements != null) && (!aiMovements.isEmpty())) {
            nuMovements += aiMovements.size();
            for (Card movement : aiMovements) {
                playCard(movement);
            }

            aiMovements = aiEntity.selectMovementsAi();
        }

        return nuMovements;
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

    public boolean playCard(Card card) {
        // Pega o jogador atual
        PlayerEntity currentPlayer = players.get(currentPlayerIndex);
        CardManager cardManager = currentPlayer.getCardManager();

        // Mana do jogador insuficiente para utilizar a carta
        if (card.getManaCost() > currentPlayer.getMana()) {
            // Mensagem caso o jogador atual seja uma entidade controlável
            if (currentPlayer instanceof ControllableEntity) {
                String message = "Mana insuficiente para utilizar " + card.getName() + ".";
                newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.SIMPLE , 1, new PlayerView(currentPlayer)));
            }

            return false;
        }

        switch (card.getCardType()) {
            case POWER:
                // Carta atual em campo
                Card currentPlayerFieldCard = currentPlayer.getFieldCard();

                // Caso já exista uma carta em campo para o jogador atual, retornamos
                if (currentPlayerFieldCard != null) {
                    if (currentPlayer instanceof ControllableEntity) {
                        String message = "Não foi possível colocar " + card.getName() + ", pois " + currentPlayerFieldCard.getName() + " já está em campo.";
                        newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.SIMPLE, 1, new PlayerView(currentPlayer)));
                    }
                    return false;
                }

                // Caso não exista uma carta em campo, utilizamos a carta
                currentPlayer.setFieldCard(card);
                cardManager.useCard(card);

                // Consumimos a mana do jogador
                currentPlayer.consumeMana(card.getManaCost());

                // Mensagem de carta em campo
                String message = currentPlayer.getName() + " colocou " + card.getName() + " em campo.";
                int target = !(currentPlayer instanceof AiEntity) ? 1 : 2;
                newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.FIELD_CARD, target, new PlayerView(currentPlayer)));

                return true;

            case EFFECT:
                // Aplicamos o efeito utilizando o handler de efeitos
                boolean used = effectHandler.handleEffect(currentPlayer, card);
                if (used) cardManager.useCard(card);

                return true;

            default:
                return false;
        }
    }

    public void discardCard(Card card) {
        PlayerEntity currentPlayer = players.get(currentPlayerIndex);
        CardManager cardManager = currentPlayer.getCardManager();

        // Descartamos a carta selecionada
        cardManager.useCard(card);

        // Mensagem de descarte
        String message = currentPlayer.getName() + " descartou " + card.getName() + ".";
        if (currentPlayer instanceof ControllableEntity) {
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.DISCARD_CARD));

        } else {
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.SIMPLE));
        }
    }

    public void updateCurrentPlayerIndex() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void updateBattleState() {
        // Verifica se algum jogador perdeu (vida menor que 1)
        PlayerEntity winner = null;

        for (PlayerEntity player : players) {
            if (player.getHp() < 1 || player.getCardManager().getDeck().isEmpty()) {
                battleState = BattleState.FINISHED;

                // Determina que o adversário é o vencedor
                winner = players.get((players.indexOf(player) + 1) % players.size());
                break;
            }
        }

        if (winner != null) {
            // Mensagem de fim de partida
            String message = winner.getName() + " venceu a batalha.";
            int target = players.indexOf(winner) + 1;
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.GAME_FINISHED, target, new PlayerView(winner)));
        }
    }

    public void consumeFieldCards() {
        for (PlayerEntity player : players) {
            player.clearFieldCard();
        }
    }

    public void handleBattlePhase() {
        // Jogadores
        PlayerEntity firstPlayer = players.get(0);
        PlayerEntity secondPlayer = players.get(1);

        // Fase de combate para determinar o vencedor da rodada
        combatPhaseHandler.handleCombatPhase(firstPlayer, secondPlayer);
        firstPlayer.clearFieldCard();
        secondPlayer.clearFieldCard();
    }

    private List<PlayerEntity> getPlayerEntities() {
        return players;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public NewBattleMessageHandler getNewBattleMessageHandler() {
        return newBattleMessageHandler;
    }

    public BattleState getBattleState() {
        return battleState;
    }
}
