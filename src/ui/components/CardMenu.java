package ui.components;

import game.cards.Card;
import game.cards.CardType;
import game.controller.GamePanel;
import ui.controllers.BattleMenuController;
import utils.SpritesHandler;
import utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CardMenu extends Menu {

    SpritesHandler spritesHandler;
    BattleMenuController battleMenuController;
    Font cardDescriptionFont = menuFont.deriveFont(24f);

    public CardMenu(GamePanel gamePanel, BattleMenuController battleMenuController) {
        super(gamePanel);
        initCardMenuDefaultValues();
        this.battleMenuController = battleMenuController;
        this.spritesHandler = gamePanel.spritesHandler;
    }

    private void initCardMenuDefaultValues() {
        menuWidth = gamePanel.screenWidth/3;
        menuHeight = 3 * gamePanel.tileSize;
        menuX = gamePanel.screenWidth/2 + 2 * gamePanel.tileSize - 16;
        menuY = gamePanel.screenHeight/2 - gamePanel.tileSize;
        borderSize = 5;
    }

    public void draw(Graphics2D g2d) {
        drawMenuWindow(g2d);
        drawCurrentSelectedCard(g2d);
    }

    public void drawCardStatus(Graphics2D g2d, Card currentSelectedCard) {
        // Caso nulo
        if (currentSelectedCard == null) {
            return;
        }

        // Coordenadas
        int tempX = menuX + gamePanel.tileSize/2;
        int tempY = menuY + gamePanel.tileSize/2 + 4 * borderSize;

        // Atributos de Poder e Dano Direto, para a carta do tipo POWER
        if (currentSelectedCard.getCardType().equals(CardType.POWER)) {
            // Poder da Carta
            BufferedImage cardElementSprite = currentSelectedCard.getCardElementSprite();
            g2d.drawImage(cardElementSprite, tempX, tempY - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);

            Color powerColor = switch(currentSelectedCard.getCardElement()) {
                case FIRE -> new Color(255, 0, 0);
                case WATER -> new Color(0, 0, 255);
                case GRASS -> new Color(0,80,0);
            };
            String powerText = currentSelectedCard.getPower().toString();

            g2d.setFont(menuFont);
            g2d.setColor(powerColor);
            g2d.drawString(powerText, tempX + gamePanel.tileSize/2 + 4, tempY);

            // Dano Direto
            tempX += (int) (menuWidth/3.5);
            BufferedImage directDamageSprite = spritesHandler.getDirectDamage();
            g2d.drawImage(directDamageSprite, tempX, tempY - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);

            Color directDamageColor = new Color(225, 80, 20);
            String directDamageText = String.valueOf(currentSelectedCard.getDirectDamage());

            g2d.setColor(directDamageColor);
            g2d.drawString(directDamageText, tempX + gamePanel.tileSize/2 + 4, tempY);

            tempX += (int) (menuWidth/3.5);
        }

        // Custo de Mana
        BufferedImage cardManaCostSprite = spritesHandler.getFilledMana();
        g2d.drawImage(cardManaCostSprite, tempX, tempY - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);

        Color manaCostColor = new Color(0, 173, 201);
        String manaCostText = String.valueOf(currentSelectedCard.getManaCost());

        g2d.setColor(manaCostColor);
        g2d.drawString(manaCostText, tempX + gamePanel.tileSize/2 + 4, tempY);
    }

    public void drawCardDescription(Graphics2D g2d, Card currentSelectedCard) {
        // Caso nulo
        if (currentSelectedCard == null) {
            return;
        }

        // Coordenadas
        int tempX = menuX + gamePanel.tileSize/2;
        int tempY = menuY + gamePanel.tileSize + 4 * borderSize;

        // Fonte e Cor dos Textos
        g2d.setFont(cardDescriptionFont);
        g2d.setColor(Color.black);

        // Separamos a descrição em linhas de no máximo 16 caracteres
        String[] linhas = Utils.breakString(currentSelectedCard.getDescription(), 16);

        // Para cada linha desenhamos na tela sua respectiva String
        for (String linha : linhas) {
            g2d.drawString(linha, tempX , tempY);
            tempY += gamePanel.tileSize/2;
        }
    }

    public void drawCurrentSelectedCard(Graphics2D g2d) {
        // Resgatamos a carta selecionada atualmente
        int currentIndex = battleMenuController.getCurrentSelectedCardIndex();
        Card currentSelectedCard = battleMenuController.getPlayerHand().get(currentIndex);

        // Caso nulo
        if (currentSelectedCard == null) {
            return;
        }

        // Desenhamos as informações da Carta na tela
        drawCardStatus(g2d, currentSelectedCard);
        drawCardDescription(g2d, currentSelectedCard);
    }
}
