package game.battle;

import game.cards.*;
import game.entity.AiEntity;
import game.entity.ControllableEntity;
import game.entity.PlayerEntity;
import ui.components.BattleMenu;
import ui.controllers.BattleMenuController;

import java.util.ArrayList;
import java.util.List;

public class Battle {

    BattleMenuController battleMenuController;

    BattleState battleState;
    List<PlayerEntity> players;
    Card[] fieldCards;
    List<Card> firstPlayerEffects, secondPlayerEffects;

    int currentTurn;
    int currentPlayerIndex;

    BattleMessageHandler battleMessageHandler;
    CombatPhaseHandler combatPhaseHandler;
    EffectHandler effectHandler;

    public Battle() {
        // estado da batalha
        battleState = BattleState.IN_PROGRESS;

        // inicializamos os jogadores
        players = new ArrayList<>();
        players.add(new ControllableEntity("NOYUR", 6, 0, "deck01.txt"));
        players.add(new AiEntity("BOT", 6, 0,"deck01.txt"));

        // inicialização de outras variáveis
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
        // entidades
        PlayerEntity firstPlayer = players.get(0);
        PlayerEntity secondPlayer = players.get(1);

        // shuffle no baralho de ambos jogadores e compra 5 cartas
        for (PlayerEntity player : players) {
            player.getCardManager().shuffleDeck();
            player.getCardManager().drawCards(5);
        }

        // mensagem de início de batalha
        String message = "Batalha iniciada entre " + firstPlayer.getName() + " e " + secondPlayer.getName() + ".";
        battleMessageHandler.sendMessage(message);

        // inicia o turno do jogador
        startPlayerTurn();
    }

    public void handleTurnStart(PlayerEntity currentPlayer) {
        CardManager cardManager = currentPlayer.getCardManager();

        // Jogador recupera mana
        if (currentPlayer.getMana() < PlayerEntity.maxMana) {
            currentPlayer.regenMana(1);
        }

        // Tentamos comprar uma carta
        int drawnCards = cardManager.drawCards(1);

        if (drawnCards > 0) {
            // Mensagem de compra de cartas
            battleMessageHandler.sendMessage(currentPlayer.getName() + " comprou " + drawnCards + " cartas(s) do baralho.");
        }

        // lidamos com o descarte de cartas extras
        handleDiscard(currentPlayer);
    }

    public void handleDiscard(PlayerEntity currentPlayer) {
        // gerenciador de cartas do jogador atual
        CardManager currentPlayerCardManager = currentPlayer.getCardManager();

        int handSize = currentPlayerCardManager.getHand().size();
        int maxHandSize = currentPlayerCardManager.handMaxCards;

        // verificamos se o número de cartas do jogador excede o tamanho da mão
        if (handSize > maxHandSize) {
            battleMessageHandler.sendMessage("Sua mão está cheia, selecione uma carta para descartar.");
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
                battleMessageHandler.sendMessage("Mana insuficiente para utilizar " + card.getName() + ".");
            }
            return false;
        }

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

                // Consumimos a mana do jogador
                currentPlayer.consumeMana(card.getManaCost());

                // Mensagem de carta em campo
                battleMessageHandler.sendMessage(currentPlayer.getName() + " colocou " + card.getName() + " em campo.");

                return true;

            case EFFECT:
                // Retiramos a carta da mão do jogador e consumimos a mana
                cardManager.useCard(card);
                currentPlayer.consumeMana(card.getManaCost());

                // Aplicamos o efeito utilizando o handler de efeitos
                effectHandler.handleEffect(currentPlayer, currentPlayerIndex, card);

                return true;

            default:
                return false;
        }
    }

    public void discardCard(Card card) {
        PlayerEntity currentPlayer = players.get(currentPlayerIndex);
        CardManager cardManager = currentPlayer.getCardManager();

        // descartamos a carta selecionada
        cardManager.useCard(card);

        // mensagem de descarte
        String message = currentPlayer.getName() + " descartou " + card.getName() + ".";
        battleMessageHandler.sendMessage(message);

        battleMenuController.setCurrentMode(BattleMenuController.BattleMenuMode.MESSAGE);
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

    private List<PlayerEntity> getPlayerEntities() {
        return players;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public BattleMessageHandler getBattleMessageHandler() {
        return battleMessageHandler;
    }

    public void setBattleMenuController(BattleMenuController battleMenuController) {
        // linkamos o controller
        this.battleMenuController = battleMenuController;

        // handler de mensagens
        battleMenuController.setBattleMessageHandler(battleMessageHandler);

        // mão do jogador
        PlayerEntity firstPlayer = players.get(0);
        List<Card> hand = firstPlayer.getCardManager().getHand();
        battleMenuController.setPlayerHand(hand);
    }

    public BattleState getBattleState() {
        return battleState;
    }
}
