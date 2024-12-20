package game.battle;

import game.cards.*;
import game.entity.AiEntity;
import game.entity.ControllableEntity;
import game.entity.PlayerEntity;
import game.message.BattleMessage;
import game.message.BattleMessageType;
import game.message.NewBattleMessageHandler;
import ui.controllers.BattleMenuController;

import java.util.ArrayList;
import java.util.List;

public class Battle {

    BattleMenuController battleMenuController;

    BattleState battleState;
    List<PlayerEntity> players;

    int currentTurn;
    int currentPlayerIndex;

    BattleMessageHandler battleMessageHandler;
    NewBattleMessageHandler newBattleMessageHandler;
    CombatPhaseHandler combatPhaseHandler;
    EffectHandler effectHandler;

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
        newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.DRAW_CARD));
//        battleMessageHandler.sendMessage(message);

        // Inicia o Turno do Jogador
        startPlayerTurn();
    }

    public void handleTurnStart(PlayerEntity currentPlayer) {
        CardManager cardManager = currentPlayer.getCardManager();

        // Jogador recupera mana
        if (currentPlayer.getMana() < PlayerEntity.maxMana) {
            currentPlayer.regenMana(1);
            int target = !(currentPlayer instanceof AiEntity) ? 1 : 2;
            String message = currentPlayer.getName() + " recuperou 1 de mana.";
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.REGEN_MANA, target));
        }

        // Tentamos comprar uma carta
        int drawnCards = cardManager.drawCards(1);

        if (drawnCards > 0) {
            // Mensagem de compra de cartas
            String message = currentPlayer.getName() + " comprou " + drawnCards + " cartas(s) do baralho.";
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.DRAW_CARD));
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
                newBattleMessageHandler.addMessage(new BattleMessage("Sua mão está cheia, selecione uma carta para descartar.", BattleMessageType.DISCARD_CARD));
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
        PlayerEntity aiEntity = players.get(currentPlayerIndex);

        // Processamento genérico de turno de um PlayerEntity
        handleTurnStart(aiEntity);

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

    public boolean playCard(Card card) {
        // Pega o jogador atual
        PlayerEntity currentPlayer = players.get(currentPlayerIndex);
        CardManager cardManager = currentPlayer.getCardManager();

        // Mana do jogador insuficiente para utilizar a carta
        if (card.getManaCost() > currentPlayer.getMana()) {
            // Mensagem caso o jogador atual seja uma entidade controlável
            if (currentPlayer instanceof ControllableEntity) {
                String message = "Mana insuficiente para utilizar " + card.getName() + ".";
                newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.SIMPLE));
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
                        newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.SIMPLE));
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
                newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.FIELD_CARD, target));

                return true;

            case EFFECT:
                // Retiramos a carta da mão do jogador e consumimos a mana
                cardManager.useCard(card);
                currentPlayer.consumeMana(card.getManaCost());

                // Aplicamos o efeito utilizando o handler de efeitos
                effectHandler.handleEffect(currentPlayer, card);

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
            if (player.getHp() < 1) {
                battleState = BattleState.FINISHED;

                // Determina que o adversário é o vencedor
                winner = players.get((players.indexOf(player) + 1) % players.size());
                break;
            }
        }

        if (winner != null) {
            // Mensagem de fim de partida
            String message = winner.getName() + " venceu a batalha.";
            newBattleMessageHandler.addMessage(new BattleMessage(message, BattleMessageType.GAME_FINISHED));
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
    }

    private List<PlayerEntity> getPlayerEntities() {
        return players;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public BattleMessageHandler getBattleMessageHandler() {
        return battleMessageHandler;
    }

    public NewBattleMessageHandler getNewBattleMessageHandler() {
        return newBattleMessageHandler;
    }

    public BattleState getBattleState() {
        return battleState;
    }
}
