package ui.component;

import game.battle.Battle;
import game.card.Card;
import game.card.CardManager;
import game.controller.GamePanel;
import game.entity.PlayerEntity;
import game.message.NewBattleMessageHandler;
import game.states.GameState;
import game.view.PlayerView;
import ui.animation.AnimationHandler;
import ui.controller.BattleMenuController;
import ui.enums.PlayerMenuType;
import ui.sprite.SpritesHandler;
import utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BattleMenu extends Menu {
    // Batalha
    Battle battle;

    // Sprites
    SpritesHandler spritesHandler;

    // UI's
    PlayerMenu playerUI, opponentUI;
    CardMenu cardMenu;

    // Controllers
    BattleMenuController battleMenuController;

    // Background
    int backgroundX = 0;
    int backgroundY = -20;

    // Animações
    AnimationHandler animationHandler;
    int turnAnimationCounter = 0;
    int cursorAnimationCounter = 0;

    public BattleMenu(GamePanel gamePanel, Battle battle) {
        super(gamePanel);
        this.spritesHandler = gamePanel.spritesHandler;
        this.animationHandler = new AnimationHandler(gamePanel);
        this.animationHandler.transitionAnimation = true;
        this.battle = battle;

        initMenus();
        startBattle();
        initBattleMenuDefaultValues();
    }

    private void startBattle() {
        battle.startBattle();
    }

    private void initMenus() {
        // Jogador #1
        PlayerEntity player = battle.getPlayers().get(0);
        PlayerView playerView = new PlayerView(player);
        this.playerUI = new PlayerMenu(gamePanel, PlayerMenuType.PLAYER, playerView);

        // Jogador #2
        PlayerEntity opponent = battle.getPlayers().get(1);
        PlayerView opponentView = new PlayerView(opponent);
        this.opponentUI = new PlayerMenu(gamePanel, PlayerMenuType.OPPONENT, opponentView);

        // Controller do Menu de Batalha
        this.battleMenuController = new BattleMenuController(gamePanel, animationHandler, gamePanel.keyHandler, battle, playerView, opponentView);

        // UI descrição de cartas
        this.cardMenu = new CardMenu(gamePanel, battleMenuController);
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
        if (animationHandler.transitionAnimation) {
            animationHandler.draw(g2d);
            return;
        }

        // Desenha o plano de fundo
        drawBackground(g2d);

        // Desenha o turno da batalha
        drawTurn(g2d);

        // Desenha a caixa de opções
        drawMenuWindow(g2d);

        // UI's do jogador e oponente
        playerUI.draw(g2d);
        opponentUI.draw(g2d);

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

        animationHandler.draw(g2d);
    }

    public void drawBackground(Graphics2D g2d) {
        BufferedImage bg = spritesHandler.getBackground();
        g2d.drawImage(bg, backgroundX, backgroundY, bg.getWidth()/3, (int)bg.getHeight()/3, null);
    }

    public void drawTurn(Graphics2D g2d) {
        turnAnimationCounter++;

        // Coordenadas
        int tempX = 5;
        int tempY = 5;

        // Fundo
        g2d.setColor(Color.orange);
        g2d.fillRoundRect(tempX, tempY, 4*gamePanel.tileSize, gamePanel.tileSize + 10, 5, 5);

        // Atualiza as coordenadas
        tempX += 5;
        tempY += 5;

        // Coconut Logo
        BufferedImage coconut = spritesHandler.getCoconut();
        g2d.drawImage(coconut, tempX, tempY, gamePanel.tileSize, gamePanel.tileSize, null);

        // Atualiza as coordenadas
        tempX += gamePanel.tileSize + 5;
        tempY += gamePanel.tileSize - 16;

        String turno = "TURNO > " + battleMenuController.getCurrentTurn();
        g2d.setFont(menuFont.deriveFont(24f));
        g2d.setColor(Color.red);

        if (turnAnimationCounter <= 20) g2d.drawString(turno, tempX, tempY); else g2d.drawString(turno, tempX, tempY + 1);

        if (turnAnimationCounter >= 40) turnAnimationCounter = 0;
    }

    public void drawBattleMenuOptions(Graphics2D g2d) {
        // Fonte e Cor dos textos
        g2d.setColor(Color.black);
        g2d.setFont(menuFont);

        int optionsSize = menuWidth/2;
        int x = menuX + gamePanel.tileSize;
        int y = menuY + menuHeight/2 + borderSize;

        g2d.drawString("MÂO", x, y);

        x = x + optionsSize;
        g2d.drawString("ENCERRAR TURNO", x, y);
    }

    public void drawBattleOptionCursor(Graphics2D g2d) {
        int optionsSize = menuWidth/2;

        int baseX = menuX + gamePanel.tileSize/2;
        int y = menuY + menuHeight/2 + borderSize;

        int x = switch(battleMenuController.getCurrentOption()) {
            case MAO -> baseX;
            case ENCERRAR -> baseX + optionsSize;
        };

        g2d.setColor(Color.black);

        // Animação para o cursor
        // a cada 15 frames o cursor pisca brevemente
        if (cursorAnimationCounter < 15) {
            g2d.drawString(">", x, y);
        }  else if (cursorAnimationCounter > 30) {
            cursorAnimationCounter = 0;
        }

        cursorAnimationCounter++;
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
        if (cursorAnimationCounter < 15) {
            g2d.drawString(">", x, y);
        }  else if (cursorAnimationCounter > 30) {
            cursorAnimationCounter = 0;
        }

        cursorAnimationCounter++;
    }

    public void drawMessageCursor(Graphics2D g2d) {
        int x = menuX + menuWidth - gamePanel.tileSize;
        int y = menuY + menuHeight - gamePanel.tileSize/2;

        g2d.setColor(Color.black);

        // Animação para o cursor
        // a cada 15 frames o cursor pisca brevemente
        if (cursorAnimationCounter < 15) {
            g2d.drawString("v", x, y);
        }  else if (cursorAnimationCounter > 30) {
            cursorAnimationCounter = 0;
        }

        cursorAnimationCounter++;
    }

    public void drawMessage(Graphics2D g2d) {
        NewBattleMessageHandler battleMessageHandler = battle.getNewBattleMessageHandler();

        // Fonte e Cor dos textos
        g2d.setColor(Color.black);
        g2d.setFont(menuFont);

        String message = battleMessageHandler.getBattleMessage();
        if (message == null) return;

        String[] lines = Utils.breakString(message, 36);

        int x = menuX + gamePanel.tileSize/2;
        int baseY = menuY + gamePanel.tileSize;

        for (int i = 0; i < lines.length; i++) {
            g2d.drawString(lines[i], x, baseY + i * gamePanel.tileSize);
        }
    }
}
