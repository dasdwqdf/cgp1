package ui.components;

import game.cards.Card;
import game.controller.GamePanel;
import game.entity.PlayerEntity;
import utils.StatusSprites;
import utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerMenu extends Menu {

    StatusSprites statusSprites;
    PlayerEntity player;
    Font playerMenuFont = menuFont.deriveFont(24f);

    public PlayerMenu(GamePanel gamePanel, PlayerMenuType playerMenuType, PlayerEntity player) {
        super(gamePanel);
        initPlayerMenuDefaultValues(playerMenuType);
        this.player = player;
        this.statusSprites = gamePanel.statusSprites;
    }

    private void initPlayerMenuDefaultValues(PlayerMenuType playerMenuType) {
        menuWidth = gamePanel.screenWidth/2;
        menuHeight = 3 * gamePanel.tileSize;

        switch (playerMenuType) {
            case PLAYER -> {
                menuX = gamePanel.tileSize;
                menuY = gamePanel.screenHeight/2 - gamePanel.tileSize;
            }

            case OPPONENT -> {
                menuX = gamePanel.screenWidth - gamePanel.tileSize - menuWidth;
                menuY = gamePanel.tileSize;
            }
        }

        borderVisible = false;
        borderSize = 0;
    }

    public void draw(Graphics2D g2d) {
        drawMenuWindow(g2d);
        drawPlayer(g2d);
    }

    public void drawPlayerName(Graphics2D g2d) {
        int tempX = menuX + gamePanel.tileSize/2;
        int tempY = menuY + gamePanel.tileSize/2;

        g2d.setColor(Color.BLACK);
        g2d.setFont(playerMenuFont);
        g2d.drawString(player.getName(), tempX, tempY);
    }

    public void drawPlayerStatus(Graphics2D g2d) {
        // Coordenadas
        int tempX = menuX + gamePanel.tileSize/2;
        int tempY = menuY + gamePanel.tileSize + 8;

        // Sprite
        BufferedImage heartSprite = statusSprites.getHeart();
        g2d.drawImage(heartSprite, tempX, tempY - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);

        // Texto
        Color hpColor = Color.RED;
        String hpText = String.valueOf(player.getHp());

        g2d.setColor(hpColor);
        g2d.drawString(hpText, tempX + gamePanel.tileSize/2 + 4, tempY);

        // Sprite Mana
        tempX += (int) (menuWidth/5);
        BufferedImage filledManaSprite = statusSprites.getFilledMana();
        BufferedImage emptyManaSprite = statusSprites.getEmptyMana();


        for (int i = 0; i < PlayerEntity.maxMana; i++) {
            if (i < player.getMana()) {
                g2d.drawImage(filledManaSprite, tempX, tempY - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);
            } else {
                g2d.drawImage(emptyManaSprite, tempX, tempY - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);
            }

            tempX += gamePanel.tileSize/2 + 4;
        }

    }

    public void drawFieldCard(Graphics2D g2d) {
        // carta em campo do jogador
        Card fieldCard = player.getFieldCard();

        // caso nao tenha carta em campo
        if (fieldCard == null) {
            return;
        }

        int tempX = menuX + gamePanel.tileSize/2;
        int tempY = menuY + 3*(gamePanel.tileSize/2);

        Color borderColor = Color.BLACK;
        Color elementColor = Utils.getFontColorPerElement(fieldCard.getCardElement());

        // retângulo da carta
        Rectangle cardRectangle = new Rectangle(tempX, tempY, gamePanel.tileSize, gamePanel.tileSize + 8);
        Rectangle borderRectangle = new Rectangle(tempX, tempY, gamePanel.tileSize, gamePanel.tileSize + 8);

        // fundo preto da carta
        g2d.setColor(elementColor);
        g2d.fill(cardRectangle);

        // borda da carta
        g2d.setColor(borderColor);
        g2d.draw(borderRectangle);

        // elemento da carta
        g2d.drawImage(fieldCard.getCardElementSprite(), tempX + 12, tempY + 16, gamePanel.tileSize/2, gamePanel.tileSize/2, null);

        // poder da carta
        g2d.setFont(menuFont);
        g2d.setColor(elementColor);
        g2d.drawString(String.valueOf(fieldCard.getTotalPower()), tempX + cardRectangle.width + 8, tempY + cardRectangle.height/2 + 8);
    }

    public void drawPlayer(Graphics2D g2d) {
        drawPlayerName(g2d);
        drawPlayerStatus(g2d);
        drawFieldCard(g2d);
    }
}
