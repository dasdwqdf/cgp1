package ui.component;

import game.controller.GamePanel;
import ui.UI;

import java.awt.*;

public class Menu extends UI {

    Font menuFont = defaultFont.deriveFont(Font.BOLD, 32);

    Color shadowColor = new Color(25, 25, 25, 255);
    Color menuColor = new Color(250, 200, 120 , 255);
    Color borderColor = new Color(235, 105, 90, 255);

    // Variáveis do Menu
    int menuWidth;
    int menuHeight;
    int menuX;
    int menuY;
    int borderSize;
    boolean borderVisible = true;

    public Menu(GamePanel gamePanel) {
        super(gamePanel);
    }

    public Menu(GamePanel gamePanel, int menuWidth, int menuHeight, int x, int y, int borderSize) {
        super(gamePanel);
        this.gamePanel = gamePanel;
        this.menuWidth = menuWidth;
        this.menuHeight = menuHeight;
        this.menuX = menuX;
        this.menuY = menuY;
        this.borderSize = borderSize;
    }

    public void drawMenuWindow(Graphics2D g2d) {

        if(borderVisible) {
            // Retangulo de shadow
            g2d.setColor(shadowColor);
            g2d.fillRoundRect(menuX +10, menuY +10, menuWidth, menuHeight, 10, 10);
        }

        // Retangulo principal
        g2d.setColor(menuColor);
        g2d.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 10, 10);

        if (borderVisible) {
            // Borda do Retangulo
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(borderSize));
            g2d.drawRoundRect(menuX + borderSize, menuY + borderSize, menuWidth -10, menuHeight -10, 10, 10);
        }

    }
}
