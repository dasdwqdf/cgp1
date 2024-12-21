package ui.controllers;

import game.battle.Battle;
import game.battle.BattleMessageHandler;
import game.battle.BattleState;
import game.cards.Card;
import game.controller.GamePanel;
import game.input.KeyHandler;
import game.message.BattleMessage;
import game.message.BattleMessageType;
import game.message.NewBattleMessageHandler;
import game.view.PlayerView;
import ui.AnimationHandler;

import java.util.List;

public class BattleMenuController {

    public boolean gameOver = false;


    GamePanel gamePanel;
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
        ENCERRAR
    }

    BattleMenuMode currentMode;
    BattleMenuOption currentOption;
    Integer currentSelectedCardIndex;

    List<Card> playerHand;
    Integer handSize;
    NewBattleMessageHandler newBattleMessageHandler;

    int currentTurn;

    public BattleMenuController(GamePanel gamePanel, AnimationHandler animationHandler, KeyHandler keyHandler, Battle battle, PlayerView playerView, PlayerView opponentView) {
        this.gamePanel = gamePanel;
        this.animationHandler = animationHandler;
        this.keyHandler = keyHandler;
        this.battle = battle;
        this.newBattleMessageHandler = battle.getNewBattleMessageHandler();
        this.playerView = playerView;
        this.opponentView = opponentView;
        this.currentMode = BattleMenuMode.MESSAGE;
        this.currentOption = BattleMenuOption.MAO;
        this.currentSelectedCardIndex = 0;
        this.playerHand = battle.getPlayers().get(0).getCardManager().getHand();
        this.currentTurn = 1;
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
            gamePanel.playSE(2);
            keyHandler.rightPressed = false;

        } else if (keyHandler.leftPressed) {
            currentSelectedCardIndex = (currentSelectedCardIndex > 0) ? currentSelectedCardIndex - 1 : handSize-1;
            gamePanel.playSE(2);
            keyHandler.leftPressed = false;

        } else if (keyHandler.zPressed) {
            currentMode = BattleMenuMode.SELECT_OPTION;
            gamePanel.playSE(2);
            keyHandler.zPressed = false;

        } else if (keyHandler.xPressed) {
            gamePanel.playSE(3);
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
            gamePanel.playSE(2);
            keyHandler.rightPressed = false;

        } else if (keyHandler.leftPressed) {
            currentSelectedCardIndex = (currentSelectedCardIndex > 0) ? currentSelectedCardIndex - 1 : handSize-1;
            gamePanel.playSE(2);
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
                case MAO -> BattleMenuOption.ENCERRAR;
                case ENCERRAR -> BattleMenuOption.MAO;
            };

            gamePanel.playSE(2);
            keyHandler.rightPressed = false;

        } else if (keyHandler.leftPressed) {
            currentOption = switch (currentOption) {
                case MAO -> BattleMenuOption.ENCERRAR;
                case ENCERRAR -> BattleMenuOption.MAO;
            };

            gamePanel.playSE(2);
            keyHandler.leftPressed = false;

        } else if (keyHandler.xPressed) {

            gamePanel.playSE(3); // Efeito

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
            gamePanel.playSE(3);
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
                // SE Dano
                gamePanel.playSE(4);

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
                // SE Mana
                gamePanel.playSE(5);

                // Animação de Mana
                animationHandler.enableManaAnimation(target);

                if (target == 1) {
                    playerView.updateMana();

                } else {
                    opponentView.updateMana();
                }

                break;

            case DRAW_CARD:
                playerView.updateNuCardsDeck();
                opponentView.updateNuCardsDeck();
                break;

            case DISCARD_CARD:
                handleDiscard();
                break;

            case FIELD_CARD:
                // SE placeCard
                gamePanel.playSE(9);

                if (target == 1) {
                    playerView.updateMana();
                    playerView.updateFieldCard();

                } else {
                    opponentView.updateMana();
                    opponentView.updateFieldCard();
                }

                break;

            case EFFECT_CARD:
                playerView.updateMana();
                opponentView.updateMana();
                break;

            case HEAL_EFFECT:
                // SE Heal
                gamePanel.playSE(6);

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
                // SE PowerUp
                gamePanel.playSE(7);

                // Animação de Power UP
                if (target == 1) {
                    animationHandler.enablePlayerPowerUpAnimation();
                    playerView.updateFieldCard();

                } else {
                    animationHandler.enableBotPowerUpAnimation();
                    opponentView.updateFieldCard();
                }

                break;

            case DRAW_EFFECT:
                if (target == 1) {
                    playerView.updateNuCardsDeck();
                    handleDiscard();
                } else {
                    opponentView.updateNuCardsDeck();
                }
                break;

            case REDRAW_EFFECT:
                break;

            case ELEMENTAL_POWER_UP:
                // SE PowerUp
                gamePanel.playSE(7);
                animationHandler.enableElementalPowerUpAnimation();
                playerView.updateFieldCard();
                break;

            case ELEMENTAL_POWER_DOWN:
                // SE PoweDown
                gamePanel.playSE(8);
                animationHandler.enableElementalPowerDownAnimation();
                playerView.updateFieldCard();
                break;

            case BATTLE_PHASE_START:
                break;

            case BATTLE_DRAW:
                break;

            case BATTLE_WIN:
                break;

            case BATTLE_PHASE_END:
                animationHandler.disablePowerUpAnimation();
                battle.consumeFieldCards();
                playerView.updateFieldCard();
                opponentView.updateFieldCard();
                currentTurn++;
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

    public int getCurrentTurn() {
        return currentTurn;
    }
}
