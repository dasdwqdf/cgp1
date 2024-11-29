package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StatusSprites {

    public BufferedImage filledMana;
    public BufferedImage emptyMana;
    public BufferedImage directDamage;
    public BufferedImage heart;


    public StatusSprites() {
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

    private void loadSprites() {
        try {
            filledMana = ImageIO.read(getClass().getResource("/status/filled-mana.png"));
            emptyMana = ImageIO.read(getClass().getResource("/status/empty-mana.png"));
            directDamage = ImageIO.read(getClass().getResource("/status/direct-damage.png"));
            heart = ImageIO.read(getClass().getResource("/status/heart.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
