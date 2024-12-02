package ui.components;

import game.battle.Battle;
import game.cards.Card;
import game.cards.CardManager;
import game.controller.GamePanel;
import game.entity.PlayerEntity;
import game.states.GameState;
import ui.controllers.BattleMenuController;
import utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BattleMenu extends Menu {

    Battle battle;
    PlayerMenu playerMenu;
    PlayerMenu opponentMenu;
    CardMenu cardMenu;
    BattleMenuController battleMenuController;
    Integer animationCounter = 0;

    public BattleMenu(GamePanel gamePanel, Battle battle) {
        super(gamePanel);
        this.battle = battle;
        this.battleMenuController = new BattleMenuController(gamePanel.keyHandler, battle);
        this.cardMenu = new CardMenu(gamePanel, battleMenuController);
        battle.setBattleMenuController(battleMenuController);
        battle.startBattle();
        initBattleMenuDefaultValues();
        initPlayersMenus();
    }

    private void initPlayersMenus() {
        PlayerEntity player = battle.getPlayers().get(0);
        playerMenu = new PlayerMenu(gamePanel, PlayerMenuType.PLAYER, player);
        PlayerEntity opponent = battle.getPlayers().get(1);
        opponentMenu = new PlayerMenu(gamePanel, PlayerMenuType.OPPONENT, opponent);
    }

    private void initBattleMenuDefaultValues() {
        menuWidth = gamePanel.screenWidth - (2 * gamePanel.tileSize);
        menuHeight = 3 * gamePanel.tileSize;
        menuX = gamePanel.tileSize;
        menuY = gamePanel.screenHeight - menuHeight - gamePanel.tileSize/2;
        borderSize = 5;
    }

    public void update(){
        battleMenuController.update();
        if (battleMenuController.gameOver) {
            gamePanel.gameState = GameState.TITLE_STATE;
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        drawMenuWindow(g2d);
        playerMenu.draw(g2d);
        opponentMenu.draw(g2d);

        switch (battleMenuController.getCurrentMode()) {
            case SELECT_OPTION :
                drawBattleMenuOptions(g2d);
                drawBattleOptionCursor(g2d);
                break;

            case SELECT_CARD, SELECT_DISCARD:
                drawHand(g2d, battleMenuController.getPlayerHand());
                drawCardCursor(g2d);
                cardMenu.draw(g2d);
                break;

            case MESSAGE:
                drawMessage(g2d);
                drawMessageCursor(g2d);
                break;
        }
    }

    public void drawBattleMenuOptions(Graphics2D g2d) {
        // Fonte e Cor dos textos
        g2d.setColor(Color.black);
        g2d.setFont(menuFont);

        int optionsSize = menuWidth/3;
        int x = menuX + gamePanel.tileSize;
        int y = menuY + menuHeight/2 + borderSize;

        g2d.drawString("MÂO", x, y);

        x = x + optionsSize;
        g2d.drawString("CHECAR", x, y);

        x = x + optionsSize;
        g2d.drawString("ENCERRAR", x, y);
    }

    public void drawBattleOptionCursor(Graphics2D g2d) {
        int optionsSize = menuWidth/3;

        int baseX = menuX + gamePanel.tileSize/2;
        int y = menuY + menuHeight/2 + borderSize;

        int x = switch(battleMenuController.getCurrentOption()) {
            case MAO -> baseX;
            case CHECAR -> baseX + optionsSize;
            case ENCERRAR -> baseX + 2 * optionsSize;
        };

        g2d.setColor(Color.black);

        // Animação para o cursor
        // a cada 15 frames o cursor pisca brevemente
        if (animationCounter < 15) {
            g2d.drawString(">", x, y);
        }  else if (animationCounter > 30) {
            animationCounter = 0;
        }

        animationCounter++;
    }

    public void drawHand(Graphics2D g2d, List<Card> hand) {

        int size = Math.min(hand.size(), CardManager.handMaxSize);

        for (int i = 0; i < size; i++) {
            Card card = hand.get(i);
            drawCard(g2d, card, i);
        }
    }

    public void drawCard(Graphics2D g2d, Card card, int slot) {
        // Fonte e Cor dos textos
        g2d.setColor(Color.black);
        g2d.setFont(menuFont);

        String cardName = card.getName();
        BufferedImage cardTypeSprite = card.getCardTypeSprite();

        int cardSize = menuWidth/3;

        int baseX = menuX + gamePanel.tileSize;
        int baseY = menuY + gamePanel.tileSize;

        int x = (slot < 3) ? baseX + slot * cardSize : baseX + (slot % 3) * cardSize;
        int y = (slot < 3) ? baseY : baseY + gamePanel.tileSize;
        g2d.drawString(cardName, x, y);

        x = x + Utils.calculateTextWidth(g2d, menuFont, cardName) + 5;
        y = y - gamePanel.tileSize/2;
        g2d.drawImage(cardTypeSprite, x, y, gamePanel.tileSize/2, gamePanel.tileSize/2, null);
    }

    public void drawCardCursor(Graphics2D g2d) {
        int cardSize = menuWidth/3;
        int baseX = menuX + gamePanel.tileSize/2;
        int baseY = menuY + gamePanel.tileSize;

        int currentSelectedCard = battleMenuController.getCurrentSelectedCardIndex();
        int x = (currentSelectedCard < 3) ? baseX + currentSelectedCard * cardSize : baseX + (currentSelectedCard % 3) * cardSize;
        int y = (currentSelectedCard < 3) ? baseY : baseY + gamePanel.tileSize;

        // Animação para o cursor
        // a cada 15 frames o cursor pisca brevemente
        if (animationCounter < 15) {
            g2d.drawString(">", x, y);
        }  else if (animationCounter > 30) {
            animationCounter = 0;
        }

        animationCounter++;
    }

    public void drawMessageCursor(Graphics2D g2d) {
        int x = menuX + menuWidth - gamePanel.tileSize;
        int y = menuY + menuHeight - gamePanel.tileSize/2;

        g2d.setColor(Color.black);

        // Animação para o cursor
        // a cada 15 frames o cursor pisca brevemente
        if (animationCounter < 15) {
            g2d.drawString("v", x, y);
        }  else if (animationCounter > 30) {
            animationCounter = 0;
        }

        animationCounter++;
    }

    public void drawMessage(Graphics2D g2d) {
        // Fonte e Cor dos textos
        g2d.setColor(Color.black);
        g2d.setFont(menuFont);

        String message = battle.getBattleMessageHandler().getMessage();
        String[] lines = Utils.breakString(message, 36);

        int x = menuX + gamePanel.tileSize/2;
        int baseY = menuY + gamePanel.tileSize;

        for (int i = 0; i < lines.length; i++) {
            g2d.drawString(lines[i], x, baseY + i * gamePanel.tileSize);
        }
    }

    public void drawPlayersStatus(Graphics2D g2d) {
        PlayerEntity player1 = battle.getPlayers().get(0);
        PlayerEntity player2 = battle.getPlayers().get(1);
        drawPlayerStatus(g2d, PlayerStatusPosition.TOP_LEFT, player1);
        drawPlayerStatus(g2d, PlayerStatusPosition.TOP_RIGHT, player2);
    }

    public void drawPlayerStatus(Graphics2D g2d, PlayerStatusPosition position, PlayerEntity player) {
        // Textos
        String playerName = player.getName();
        String playerHp = String.valueOf(player.getHp());

        // Tamanho do texto
        int textWidth = Utils.calculateTextWidth(g2d, menuFont, playerName);

        // Posição
        int x = position.getX(gamePanel.getWidth(), textWidth);
        int y = position.getY();

        // Fonte e Cor dos textos
        g2d.setColor(Color.black);
        g2d.setFont(menuFont);

        g2d.drawString(playerName, x, y);
        g2d.drawString(playerHp, x, y + gamePanel.tileSize/2 + 8);
    }
}
