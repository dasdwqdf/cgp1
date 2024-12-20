package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpritesHandler {

    public BufferedImage filledMana;
    public BufferedImage emptyMana;
    public BufferedImage directDamage;
    public BufferedImage heart;
    public BufferedImage playerIdle0;
    public BufferedImage playerIdle1;
    public BufferedImage playerDamage;
    public BufferedImage playerHeal;
    public BufferedImage playerMana0;
    public BufferedImage playerMana1;
    public BufferedImage playerMana2;
    public BufferedImage playerMana3;
    public BufferedImage playerMana4;
    public BufferedImage botIdle0;
    public BufferedImage botIdle1;
    public BufferedImage botDamage;
    public BufferedImage botHeal;
    public BufferedImage botMana0;
    public BufferedImage botMana1;
    public BufferedImage botMana2;
    public BufferedImage botMana3;
    public BufferedImage botMana4;

    public SpritesHandler() {
        loadSprites();
    }

    public BufferedImage getFilledMana() {
        return filledMana;
    }

    public BufferedImage getEmptyMana() {
        return emptyMana;
    }

    public BufferedImage getDirectDamage() {
        return directDamage;
    }

    public BufferedImage getHeart() {
        return heart;
    }

    public BufferedImage getPlayerIdle0() {
        return playerIdle0;
    }

    public BufferedImage getPlayerIdle1() {
        return playerIdle1;
    }

    public BufferedImage getPlayerDamage() {
        return playerDamage;
    }

    public BufferedImage getPlayerHeal() {
        return playerHeal;
    }

    public BufferedImage getPlayerMana0() {
        return playerMana0;
    }

    public BufferedImage getPlayerMana1() {
        return playerMana1;
    }

    public BufferedImage getPlayerMana2() {
        return playerMana2;
    }

    public BufferedImage getPlayerMana3() {
        return playerMana3;
    }

    public BufferedImage getPlayerMana4() {
        return playerMana4;
    }

    public BufferedImage getBotIdle0() {
        return botIdle0;
    }

    public BufferedImage getBotIdle1() {
        return botIdle1;
    }

    public BufferedImage getBotDamage() {
        return botDamage;
    }

    public BufferedImage getBotHeal() {
        return botHeal;
    }

    public BufferedImage getBotMana0() {
        return botMana0;
    }

    public BufferedImage getBotMana1() {
        return botMana1;
    }

    public BufferedImage getBotMana2() {
        return botMana2;
    }

    public BufferedImage getBotMana3() {
        return botMana3;
    }

    public BufferedImage getBotMana4() {
        return botMana4;
    }

    private void loadSprites() {
        try {
            filledMana = ImageIO.read(getClass().getResource("/status/filled-mana.png"));
            emptyMana = ImageIO.read(getClass().getResource("/status/empty-mana.png"));
            directDamage = ImageIO.read(getClass().getResource("/status/direct-damage.png"));
            heart = ImageIO.read(getClass().getResource("/status/heart.png"));
            playerIdle0 = ImageIO.read(getClass().getResource("/characters/player-idle-0.png"));
            playerIdle1 = ImageIO.read(getClass().getResource("/characters/player-idle-1.png"));
            playerDamage = ImageIO.read(getClass().getResource("/characters/player-damage.png"));
            playerHeal = ImageIO.read(getClass().getResource("/characters/player-heal.png"));
            playerMana0 = ImageIO.read(getClass().getResource("/characters/player-mana-0.png"));
            playerMana1 = ImageIO.read(getClass().getResource("/characters/player-mana-1.png"));
            playerMana2 = ImageIO.read(getClass().getResource("/characters/player-mana-2.png"));
            playerMana3 = ImageIO.read(getClass().getResource("/characters/player-mana-3.png"));
            playerMana4 = ImageIO.read(getClass().getResource("/characters/player-mana-4.png"));
            botIdle0 = ImageIO.read(getClass().getResource("/characters/bot-idle-0.png"));
            botIdle1 = ImageIO.read(getClass().getResource("/characters/bot-idle-1.png"));
            botDamage = ImageIO.read(getClass().getResource("/characters/bot-damage.png"));
            botHeal = ImageIO.read(getClass().getResource("/characters/bot-heal.png"));
            botMana0 = ImageIO.read(getClass().getResource("/characters/bot-mana-0.png"));
            botMana1 = ImageIO.read(getClass().getResource("/characters/bot-mana-1.png"));
            botMana2 = ImageIO.read(getClass().getResource("/characters/bot-mana-2.png"));
            botMana3 = ImageIO.read(getClass().getResource("/characters/bot-mana-3.png"));
            botMana4 = ImageIO.read(getClass().getResource("/characters/bot-mana-4.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
