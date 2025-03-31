package ui;

import game.controller.GamePanel;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class UI {

    public GamePanel gamePanel;
    public Font defaultFont;

    public UI(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.loadFont();
    }

    public void loadFont() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/font/font.ttf");
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D graphics2D) {

    }

    public void drawText(Graphics2D graphics2D, Font font, String text, Integer x, Integer y) {
        graphics2D.setFont(font);
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(text, x, y);
    }

    public Integer getCenterCoordinateForText(Graphics2D graphics2D, Font font, String text) {
        FontMetrics fm = graphics2D.getFontMetrics(font);
        int textWidth = (int) fm.getStringBounds(text, graphics2D).getWidth();
        return (gamePanel.getWidth() / 2) - (textWidth / 2);
    }
}
