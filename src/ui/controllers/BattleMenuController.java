package ui.controllers;

import game.battle.Battle;
import game.battle.BattleMessageHandler;
import game.battle.BattleState;
import game.cards.Card;
import game.input.KeyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleMenuController {

    Battle battle;

    // Handler de Input's
    KeyHandler keyHandler;

    public enum BattleMenuMode {
        SELECT_OPTION,
        SELECT_CARD,
        MESSAGE
    }

    public enum BattleMenuOption {
        MAO,
        CHECAR,
        ENCERRAR
    }

    // Variaveis de controle de menu
    BattleMenuMode currentMode = BattleMenuMode.MESSAGE;
    BattleMenuOption currentOption = BattleMenuOption.MAO;
    Integer currentSelectedCardIndex = 0;

    // Variaveis auxiliares
    int handSize;
    List<Card> playerHand;
    BattleMessageHandler battleMessageHandler;

    public BattleMenuController(KeyHandler keyHandler, Battle battle) {
        this.keyHandler = keyHandler;
        this.battle = battle;
        this.playerHand = battle.getPlayers().get(0).getCardManager().getHand();
        this.handSize = playerHand.size();
        this.battleMessageHandler = battle.getBattleMessageHandler();
    }

    public void update() {
        switch (currentMode) {
            case SELECT_OPTION:
                handleSelectOption();
                break;

            case SELECT_CARD:
                handleSelectCard();
                break;

            case MESSAGE:
                handleMessage();
                break;
        }
    }

    public boolean isGameOver() {
        return battle.getBattleState().equals(BattleState.FINISHED) && !currentMode.equals(BattleMenuController.BattleMenuMode.MESSAGE);
    }

    private void handleSelectCard() {
        if (keyHandler.rightPressed) {
            currentSelectedCardIndex = (currentSelectedCardIndex < handSize-1) ? currentSelectedCardIndex + 1 : 0;
            keyHandler.rightPressed = false;

        } else if (keyHandler.leftPressed) {
            currentSelectedCardIndex = (currentSelectedCardIndex > 0) ? currentSelectedCardIndex - 1 : handSize-1;
            keyHandler.leftPressed = false;

        } else if (keyHandler.zPressed) {
            currentMode = BattleMenuMode.SELECT_OPTION;
            keyHandler.zPressed = false;

        } else if (keyHandler.xPressed) {
            Card currentCard = playerHand.get(currentSelectedCardIndex);
            battle.playCard(currentCard);
            handSize = playerHand.size();
            currentSelectedCardIndex = 0;
            currentMode = BattleMenuMode.MESSAGE;

            keyHandler.xPressed = false;
        }
    }

    private void handleSelectOption() {
        if (keyHandler.rightPressed) {
            currentOption = switch (currentOption) {
                case MAO -> BattleMenuOption.CHECAR;
                case CHECAR -> BattleMenuOption.ENCERRAR;
                case ENCERRAR -> BattleMenuOption.MAO;
            };

            keyHandler.rightPressed = false;

        } else if (keyHandler.leftPressed) {
            currentOption = switch (currentOption) {
                case MAO -> BattleMenuOption.ENCERRAR;
                case CHECAR -> BattleMenuOption.MAO;
                case ENCERRAR -> BattleMenuOption.CHECAR;
            };

            keyHandler.leftPressed = false;

        } else if (keyHandler.xPressed) {
            if (currentOption.equals(BattleMenuOption.MAO)) {
                if (handSize != 0) {
                    currentMode = BattleMenuMode.SELECT_CARD;
                } else {
                    battleMessageHandler.sendMessage("Sua mão está vazia no momento.");
                    currentMode = BattleMenuMode.MESSAGE;
                }

            } else if (currentOption.equals(BattleMenuOption.ENCERRAR)) {
                battle.endTurn();
                currentMode = BattleMenuMode.MESSAGE;
            }

            keyHandler.xPressed = false;
        }
    }

    private void handleMessage() {

        if (battleMessageHandler.isEmpty()) {
            currentMode = BattleMenuMode.SELECT_OPTION;
        }

        if (keyHandler.xPressed) {
            if (battleMessageHandler.size() == 1) {
                currentOption = BattleMenuOption.MAO;
                currentMode = BattleMenuMode.SELECT_OPTION;
                handSize = playerHand.size();
            }

            battleMessageHandler.consumeMessage();
            keyHandler.xPressed = false;
        }
    }

    public BattleMenuOption getCurrentOption() {
        return currentOption;
    }

    public BattleMenuMode getCurrentMode() {
        return currentMode;
    }

    public Integer getCurrentSelectedCardIndex() {
        return currentSelectedCardIndex;
    }

    public int getHandSize() {
        return handSize;
    }

    public List<Card> getPlayerHand() {
        return playerHand;
    }
}
