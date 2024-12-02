package ui.controllers;

import game.battle.Battle;
import game.battle.BattleMessageHandler;
import game.battle.BattleState;
import game.cards.Card;
import game.input.KeyHandler;

import java.util.List;

public class BattleMenuController {

    public boolean gameOver = false;

    Battle battle;
    KeyHandler keyHandler;

    public enum BattleMenuMode {
        SELECT_OPTION,
        SELECT_CARD,
        SELECT_DISCARD,
        MESSAGE
    }
    public enum BattleMenuOption {
        MAO,
        CHECAR,
        ENCERRAR
    }

    BattleMenuMode currentMode;
    BattleMenuOption currentOption;
    Integer currentSelectedCardIndex;

    List<Card> playerHand;
    Integer handSize;
    BattleMessageHandler battleMessageHandler;

    public BattleMenuController(KeyHandler keyHandler, Battle battle) {
        this.keyHandler = keyHandler;
        this.battle = battle;
        this.currentMode = BattleMenuMode.MESSAGE;
        this.currentOption = BattleMenuOption.MAO;
        this.currentSelectedCardIndex = 0;
    }

    public void update() {
        switch (currentMode) {
            case SELECT_OPTION:
                handleSelectOption();
                break;

            case SELECT_CARD:
                handleSelectCard();
                break;

            case SELECT_DISCARD:
                handleDiscardCard();
                break;

            case MESSAGE:
                handleMessage();
                break;
        }
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

    private void handleDiscardCard() {
        if (keyHandler.rightPressed) {
            currentSelectedCardIndex = (currentSelectedCardIndex < handSize-1) ? currentSelectedCardIndex + 1 : 0;
            keyHandler.rightPressed = false;

        } else if (keyHandler.leftPressed) {
            currentSelectedCardIndex = (currentSelectedCardIndex > 0) ? currentSelectedCardIndex - 1 : handSize-1;
            keyHandler.leftPressed = false;

        } else if (keyHandler.xPressed) {
            Card currentCard = playerHand.get(currentSelectedCardIndex);
            battle.discardCard(currentCard);
            handSize = playerHand.size();
            currentSelectedCardIndex = 0;

            keyHandler.xPressed = false;
        }
    }

    public void setCurrentMode(BattleMenuMode mode) {
        this.currentMode = mode;
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
                battle.endPlayerTurn();
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
                handleGameOver();
                handleDiscard();
            }

            battleMessageHandler.consumeMessage();
            keyHandler.xPressed = false;
        }
    }

    private void handleDiscard() {
        handSize = playerHand.size();
        currentSelectedCardIndex = 0;

        if (handSize > 6) {
            currentMode = BattleMenuMode.SELECT_DISCARD;
        } else {
            currentOption = BattleMenuOption.MAO;
            currentMode = BattleMenuMode.SELECT_OPTION;
        }
    }

    private void handleGameOver() {
        gameOver = isGameOver();
    }

    public boolean isGameOver() {
        return battle.getBattleState().equals(BattleState.FINISHED);
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

    public List<Card> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(List<Card> playerHand) {
        this.playerHand = playerHand;
    }

    public void setBattleMessageHandler(BattleMessageHandler battleMessageHandler) {
        this.battleMessageHandler = battleMessageHandler;
    }
}
