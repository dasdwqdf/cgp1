package ui;

import game.battle.Battle;
import game.controller.GamePanel;
import ui.component.BattleMenu;

import java.awt.*;

public class BattleMenuUI extends UI {

    Battle battle;
    BattleMenu battleMenu;

    public BattleMenuUI(GamePanel gamePanel) {
        super(gamePanel);
    }

    public void newBattle() {
        this.battle = new Battle();
        this.battleMenu = new BattleMenu(gamePanel, battle);
    }


    public void update() {
        battleMenu.update();
    }

    public void draw(Graphics2D g2d) {
//        drawBackground(g2d);
        battleMenu.draw(g2d);
    }


}
