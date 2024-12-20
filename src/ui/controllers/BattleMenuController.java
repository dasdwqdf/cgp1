package ui.controllers;

import game.battle.Battle;
import game.battle.BattleMessageHandler;
import game.battle.BattleState;
import game.cards.Card;
import game.input.KeyHandler;
import game.message.BattleMessage;
import game.message.BattleMessageType;
import game.message.NewBattleMessageHandler;
import game.view.PlayerView;
import ui.AnimationHandler;
import ui.components.PlayerMenu;

import java.awt.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class BattleMenuController {

    public boolean gameOver = false;

    AnimationHandler animationHandler;
    KeyHandler keyHandler;

    Battle battle;
    PlayerView playerView, opponentView;

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
    NewBattleMessageHandler newBattleMessageHandler;

    public BattleMenuController(AnimationHandler animationHandler, KeyHandler keyHandler, Battle battle, PlayerView playerView, PlayerView opponentView) {
        this.animationHandler = animationHandler;
        this.keyHandler = keyHandler;
        this.battle = battle;
        this.battleMessageHandler = battle.getBattleMessageHandler();
        this.newBattleMessageHandler = battle.getNewBattleMessageHandler();
        this.playerView = playerView;
        this.opponentView = opponentView;
        this.currentMode = BattleMenuMode.MESSAGE;
        this.currentOption = BattleMenuOption.MAO;
        this.currentSelectedCardIndex = 0;
        this.playerHand = battle.getPlayers().get(0).getCardManager().getHand();
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
                // Atualizamos a variável de controle do tamanho da mão
                this.handSize = Math.min(playerHand.size(), 6);

                if (handSize != 0) {
                    currentMode = BattleMenuMode.SELECT_CARD;

                } else {
                    newBattleMessageHandler.addMessage(new BattleMessage("Sua mão está vazia no momento!", BattleMessageType.SIMPLE));
                    currentMode = BattleMenuMode.MESSAGE;
                }

            } else if (currentOption.equals(BattleMenuOption.ENCERRAR)) {
                battle.endPlayerTurn();
                currentOption = BattleMenuOption.MAO;
                currentMode = BattleMenuMode.MESSAGE;
            }

            keyHandler.xPressed = false;
        }
    }

    private void handleMessage() {
        if (newBattleMessageHandler.isBattleMessageListEmpty()) {
            currentMode = BattleMenuMode.SELECT_OPTION;
        }

        if (keyHandler.xPressed) {
            BattleMessage battleMessage = newBattleMessageHandler.consumeMessage();
            handleMessageType(battleMessage);
            keyHandler.xPressed = false;
        }
    }

    private void handleMessageType(BattleMessage battleMessage) {
        // Resgatamos o tipo da mensagem de batalha
        BattleMessageType battleMessageType = battleMessage.getType();

        // O alvo da aplicação do efeito
        int target = battleMessage.getTarget();

        System.out.println(battleMessage);

        switch (battleMessageType) {
            case SIMPLE:
                break;

            case DAMAGE:
                // Animação de Dano
                animationHandler.enableDamageAnimation(target);

                if (target == 1) {
                    // Animação de Dano
                    playerView.updateHp();

                } else {
                    opponentView.updateHp();
                }

                break;

            case REGEN_MANA:
                // Animação de Mana
                animationHandler.enableManaAnimation(target);

                if (target == 1) {
                    playerView.updateMana();

                } else {
                    opponentView.updateMana();
                }

                break;

            case DRAW_CARD:
                break;

            case DISCARD_CARD:
                handleDiscard();
                break;

            case FIELD_CARD:
                if (target == 1) {
                    playerView.updateMana();
                    playerView.updateFieldCard();

                } else {
                    opponentView.updateMana();
                    opponentView.updateFieldCard();
                }

                break;

            case EFFECT_CARD:
                if (target == 1) {
                    // mais código depois aqui
                    playerView.updateMana();

                } else {
                    opponentView.updateMana();
                }

                break;

            case HEAL_EFFECT:
                // Animação de Heal
                animationHandler.enableHealAnimation(target);

                if (target == 1) {
                    // mais código
                    playerView.updateHp();

                } else {
                    opponentView.updateHp();
                }

                break;

            case POWER_UP_EFFECT:
                if (target == 1) {
                    playerView.updateFieldCard();

                } else {
                    opponentView.updateFieldCard();
                }

                break;

            case DRAW_EFFECT:
                handleDiscard();
                break;

            case REDRAW_EFFECT:
                break;

            case BATTLE_PHASE_START:
                break;

            case BATTLE_DRAW:
                break;

            case BATTLE_WIN:
                break;

            case BATTLE_PHASE_END:
                battle.consumeFieldCards();
                playerView.updateFieldCard();
                opponentView.updateFieldCard();
                break;

            case GAME_FINISHED:
                handleGameOver();
                break;
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
}
