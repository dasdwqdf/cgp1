package ui.components;

import game.cards.Card;
import game.controller.GamePanel;
import game.entity.PlayerEntity;
import game.view.PlayerView;
import utils.CharacterSpriteType;
import utils.SpritesHandler;
import utils.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerMenu extends Menu {

    int marginX = 10;

    SpritesHandler spritesHandler;
    CharacterSpriteType characterSpriteType;
    PlayerView playerView;
    Font playerMenuFont = menuFont.deriveFont(24f);

    // Variáveis para animação
    int currentSpriteIndex = 0;
    int totalSprites = 2;
    int animationCounter = 0;
    int animationSpeed = 20;


    public PlayerMenu(GamePanel gamePanel, PlayerMenuType playerMenuType, PlayerView playerView) {
        super(gamePanel);
        initPlayerMenuDefaultValues(playerMenuType);
        this.playerView = playerView;
        this.spritesHandler = gamePanel.spritesHandler;
    }

    private void initPlayerMenuDefaultValues(PlayerMenuType playerMenuType) {
        menuWidth = gamePanel.screenWidth/2;
        menuHeight = 3 * gamePanel.tileSize;

        switch (playerMenuType) {
            case PLAYER -> {
                menuX = gamePanel.tileSize;
                menuY = gamePanel.screenHeight/2 - gamePanel.tileSize;
                characterSpriteType = CharacterSpriteType.PLAYER;
            }

            case OPPONENT -> {
                menuX = gamePanel.screenWidth - gamePanel.tileSize - menuWidth;
                menuY = gamePanel.tileSize;
                characterSpriteType = CharacterSpriteType.BOT;
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
        int tempX = menuX + gamePanel.tileSize * 2 + marginX;
        int tempY = menuY + gamePanel.tileSize/2;

        g2d.setColor(Color.BLACK);
        g2d.setFont(playerMenuFont);
        g2d.drawString(playerView.getName(), tempX, tempY);
    }

    public void drawPlayerAvatar(Graphics2D g2d) {
        animationCounter++;
        if (animationCounter >= animationSpeed) {
          currentSpriteIndex = (currentSpriteIndex + 1) % totalSprites;
          animationCounter = 0;
        }

        // Coordenadas
        int tempX = menuX;
        int tempY = menuY;

        BufferedImage characterPortrait = getAvatarPortrait();
        g2d.drawImage(characterPortrait, tempX, tempY, gamePanel.tileSize * 2, gamePanel.tileSize * 2, null);
    }

    private BufferedImage getAvatarPortrait() {
        BufferedImage characterSprite;

        if (characterSpriteType == CharacterSpriteType.PLAYER) {
            characterSprite = switch (currentSpriteIndex) {
                case 0 -> spritesHandler.getPlayerIdle0();
                case 1 -> spritesHandler.getPlayerIdle1();
                default -> throw new IllegalStateException("Índice de sprite inválido: " + currentSpriteIndex);
            };
        } else {
            characterSprite = switch (currentSpriteIndex) {
                case 0 -> spritesHandler.getBotIdle0();
                case 1 -> spritesHandler.getBotIdle1();
                default -> throw new IllegalStateException("Índice de sprite inválido: " + currentSpriteIndex);
            };
        }
        return characterSprite;
    }

    public void drawPlayerStatus(Graphics2D g2d) {
        // Coordenadas
        int tempX = menuX + 2 * gamePanel.tileSize + marginX;
        int tempY = menuY + gamePanel.tileSize + 8;

        // Sprite
        BufferedImage heartSprite = spritesHandler.getHeart();
        g2d.drawImage(heartSprite, tempX, tempY - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);

        // Texto
        Color hpColor = Color.RED;
        String hpText = String.valueOf(playerView.getHp());

        g2d.setColor(hpColor);
        g2d.drawString(hpText, tempX + gamePanel.tileSize/2 + 4, tempY);

        // Sprite Mana
        tempX += (int) (menuWidth/6);
        BufferedImage filledManaSprite = spritesHandler.getFilledMana();
        BufferedImage emptyManaSprite = spritesHandler.getEmptyMana();


        for (int i = 0; i < PlayerEntity.maxMana; i++) {
            if (i < playerView.getMana()) {
                g2d.drawImage(filledManaSprite, tempX, tempY - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);
            } else {
                g2d.drawImage(emptyManaSprite, tempX, tempY - gamePanel.tileSize/2, gamePanel.tileSize/2, gamePanel.tileSize/2, null);
            }

            tempX += gamePanel.tileSize/2 + 4;
        }

        // Sprite Deck
        BufferedImage deckSprite = spritesHandler.getDeck();
        g2d.drawImage(deckSprite, tempX + marginX, tempY - (gamePanel.tileSize - 10), gamePanel.tileSize, gamePanel.tileSize, null);

        tempX += gamePanel.tileSize;

        // Texto NuCartas do Deck
        String nuCardsDeck = "x" + String.valueOf(playerView.getNuCardsDeck());
        g2d.drawString(nuCardsDeck, tempX + marginX, tempY);
    }

    public void drawFieldCard(Graphics2D g2d) {
        // carta em campo do jogador
        Card fieldCard = playerView.getFieldCard();

        // caso nao tenha carta em campo
        if (fieldCard == null) {
            return;
        }

        int tempX = menuX + 2 * gamePanel.tileSize + marginX;
        int tempY = menuY + 3 * (gamePanel.tileSize/2);

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
        drawPlayerAvatar(g2d);
    }
}
