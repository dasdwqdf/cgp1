package ui;

import game.battle.Battle;
import game.controller.GamePanel;
import ui.components.BattleMenu;

import java.awt.*;

public class BattleMenuUI extends UI {

    Battle battle;
    BattleMenu battleMenu;
    Color backgroundColor = new Color(225,225,200);

    public BattleMenuUI(GamePanel gamePanel) {
        super(gamePanel);
    }

    public void newBattle() {
        this.battle = new Battle();
        battleMenu = new BattleMenu(gamePanel, battle);
    }


    public void update() {
        battleMenu.update();
    }

    public void draw(Graphics2D g2d) {
        drawBackground(g2d);
        battleMenu.draw(g2d);
    }

    public void drawBackground(Graphics2D g2d) {
        int width = gamePanel.screenWidth;
        int height = gamePanel.screenHeight;
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, width, height);
    }
}
