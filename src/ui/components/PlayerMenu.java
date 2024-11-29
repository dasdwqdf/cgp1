package ui.components;

import game.controller.GamePanel;
import game.entity.PlayerEntity;
import utils.StatusSprites;

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
        int tempX = menuX + gamePanel.tileSize/2;
        int tempY = menuY + gamePanel.tileSize;

        BufferedImage heartSprite = statusSprites.getHeart();
    }

    public void drawPlayer(Graphics2D g2d) {
        drawPlayerName(g2d);
    }
}
