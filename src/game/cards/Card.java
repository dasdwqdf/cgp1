package game.cards;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Card {

    String name;
    int manaCost;
    CardType cardType;
    String description;

    // Carta de Poder
    CardElement cardElement;
    Integer power;

    // Carta de Efeito
    CardEffect cardEffect;
    Integer effectArg;

    // Sprites
    BufferedImage cardTypeSprite;
    BufferedImage cardElementSprite;

    public Card(String name, int manaCost, CardEffect cardEffect, Integer effectArg, String description) {
        this.name = name;
        this.manaCost = manaCost;
        this.cardType = CardType.EFFECT;
        this.cardEffect = cardEffect;
        this.effectArg = effectArg;
        this.description = description;
        this.cardTypeSprite = loadTypeSprite();
    }

    public Card(String name, int manaCost, CardElement cardElement, Integer Power, String description) {
        this.name = name;
        this.manaCost = manaCost;
        this.cardType = CardType.POWER;
        this.cardElement = cardElement;
        this.power = Power;
        this.description = description;
        this.cardTypeSprite = loadTypeSprite();
        this.cardElementSprite = loadElementSprite();
    }

    private BufferedImage loadTypeSprite() {
        BufferedImage image = null;
        String urlImage = switch(cardType) {
            case POWER -> "/card-type/power.png";
            case EFFECT -> "/card-type/effect.png";
        };

        try {
            image = ImageIO.read(getClass().getResource(urlImage));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private BufferedImage loadElementSprite() {
        BufferedImage image = null;
        String urlImage = switch(cardElement) {
            case FIRE -> "/card-element/fire.png";
            case WATER -> "/card-element/water.png";
            case GRASS -> "/card-element/grass.png";
        };

        try {
            image = ImageIO.read(getClass().getResource(urlImage));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public String getName() {
        return name;
    }

    public CardType getCardType() {
        return cardType;
    }

    public String getDescription() {
        return description;
    }

    public CardElement getCardElement() {
        return cardElement;
    }

    public Integer getPower() {
        return power;
    }

    public CardEffect getCardEffect() {
        return cardEffect;
    }

    public Integer getEffectArg() {
        return effectArg;
    }

    public BufferedImage getCardTypeSprite() {
        return cardTypeSprite;
    }

    public BufferedImage getCardElementSprite() {
        return cardElementSprite;
    }

    @Override
    public String toString() {
        return "Card{" +
                "name='" + name + '\'' +
                ", cardType=" + cardType +
                ", description='" + description + '\'' +
                ", cardElement=" + cardElement +
                ", power=" + power +
                ", cardEffect=" + cardEffect +
                ", effectArg=" + effectArg +
                '}';
    }
}
