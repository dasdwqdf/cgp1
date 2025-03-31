package utils;

import game.card.CardElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static Integer calculateTextWidth(Graphics2D g2d, Font font, String text) {
        FontMetrics fm = g2d.getFontMetrics(font);
        return fm.stringWidth(text);
    }

    public static Integer getCenterCoordinateForText(Graphics2D graphics2D, Font font, String text, Integer width) {
        Integer textWidth = calculateTextWidth(graphics2D, font, text);
        return (width) - (textWidth / 2);
    }

    public static String[] breakString(String input, int size) {
        String[] words = input.split(" "); // Divide a string em palavras
        StringBuilder currentPart = new StringBuilder();
        List<String> parts = new ArrayList<>(); // Usando uma lista para facilitar o tamanho dinâmico

        for (String word : words) {
            // Verifica se adicionar a palavra excede o limite de 16 caracteres
            if (currentPart.length() + word.length() + 1 > size) {
                // Adiciona a parte atual à lista e reseta
                parts.add(currentPart.toString().trim());
                currentPart.setLength(0); // Reseta o StringBuilder
            }

            // Adiciona a palavra à parte atual
            if (currentPart.length() > 0) {
                currentPart.append(" "); // Adiciona espaço se não for a primeira palavra
            }
            currentPart.append(word);
        }

        // Adiciona a última parte, se existir
        if (currentPart.length() > 0) {
            parts.add(currentPart.toString().trim());
        }

        // Converte a lista em um array
        return parts.toArray(new String[0]);
    }

    public static Color getFontColorPerElement(CardElement cardElement) {
        return switch (cardElement) {
            case FIRE -> Color.RED;
            case WATER -> Color.BLUE;
            case GRASS -> new Color(0,180,0);
            default -> null;
        };
    }
}
