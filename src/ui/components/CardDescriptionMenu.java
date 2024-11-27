package ui.components;

import game.cards.Card;
import game.cards.CardType;
import game.controller.GamePanel;
import ui.controllers.BattleMenuController;
import utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CardDescriptionMenu extends Menu {

    BattleMenuController battleMenuController;
    Font cardDescriptionFont = menuFont.deriveFont(24f);

    public CardDescriptionMenu(GamePanel gamePanel, BattleMenuController battleMenuController) {
        super(gamePanel);
        initCardDescriptionMenuDefaultValues();
        this.battleMenuController = battleMenuController;
    }

    private void initCardDescriptionMenuDefaultValues() {
        menuWidth = gamePanel.screenWidth/3;
        menuHeight = 3 * gamePanel.tileSize;
        menuX = gamePanel.screenWidth/2 + 2 * gamePanel.tileSize - 16;
        menuY = gamePanel.screenHeight/2 - gamePanel.tileSize;
        borderSize = 5;
    }

    public void draw(Graphics2D g2d) {
        drawMenuWindow(g2d);
        drawCurrentSelectedCardDescription(g2d);
    }

    public void drawCurrentSelectedCardDescription(Graphics2D g2d) {
        int currentIndex = battleMenuController.getCurrentSelectedCardIndex();
        Card currentSelectedCard = battleMenuController.getPlayerHand().get(currentIndex);

        if (currentSelectedCard != null) {
            // Fonte e Cor dos textos
            g2d.setColor(Color.black);
            g2d.setFont(cardDescriptionFont);
            int x = menuX + gamePanel.tileSize/2;
            int y = menuY + gamePanel.tileSize/2 + 4 * borderSize;

            if (currentSelectedCard.getCardType().equals(CardType.POWER)) {
                BufferedImage cardElementSprite = currentSelectedCard.getCardElementSprite();
                g2d.drawImage(cardElementSprite, x, y - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);

                Color powerColor = switch(currentSelectedCard.getCardElement()) {
                    case FIRE -> new Color(255, 0, 0);
                    case WATER -> new Color(0, 0, 255);
                    case GRASS -> new Color(0,80,0);
                };

                String powerText = currentSelectedCard.getPower().toString();

                g2d.setFont(menuFont);
                g2d.setColor(powerColor);
                g2d.drawString(powerText, x + gamePanel.tileSize/2 + 4, y);

                y += gamePanel.tileSize/2;
            }

            g2d.setFont(cardDescriptionFont);
            g2d.setColor(Color.black);

            String[] linhas = Utils.breakString(currentSelectedCard.getDescription(), 16);
            for (String linha : linhas) {
                g2d.drawString(linha, x , y);
                y += gamePanel.tileSize/2;
            }

        }
    }

}
