package ui;

import game.controller.GamePanel;
import game.states.GameState;
import game.input.KeyHandler;
import title.SelectedOption;

import java.awt.*;

public class MainMenuUI extends UI {

    KeyHandler keyHandler;
    Font titleFont = defaultFont.deriveFont(Font.BOLD, 40);
    Font menuBarFont = defaultFont.deriveFont(Font.PLAIN, 25F);
    SelectedOption currentSelected = SelectedOption.PLAY;

    public MainMenuUI(GamePanel gamePanel) {
        super(gamePanel);
        this.keyHandler = gamePanel.keyHandler;
    }

    public void update() {
        if (keyHandler.upPressed || keyHandler.downPressed) {
            currentSelected = (currentSelected != SelectedOption.PLAY) ? SelectedOption.PLAY : SelectedOption.QUIT;
            keyHandler.upPressed = false;
            keyHandler.downPressed = false;

        } else if (keyHandler.enterPressed) {

            if (currentSelected == SelectedOption.PLAY) {
                gamePanel.stopMusic();
                gamePanel.playSE(3);
                gamePanel.playMusic(1);
                gamePanel.battleMenuUI.newBattle();
                gamePanel.gameState = GameState.PLAY_STATE;

            } else if (currentSelected == SelectedOption.QUIT) {
                gamePanel.gameState = GameState.EXIT_STATE;
            }

            keyHandler.enterPressed = false;
        }
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        this.drawTitleName(graphics2D);
        this.drawMenu(graphics2D);
    }

    public void drawTitleName(Graphics2D graphics2D) {
        String title = "Card";
        Integer x = getCenterCoordinateForText(graphics2D, titleFont, title);
        Integer y = gamePanel.tileSize * 3;
        super.drawText(graphics2D, titleFont, title, x, y);
    }

    public void drawMenu(Graphics2D graphics2D) {
        String text = "Jogar";
        Integer x = getCenterCoordinateForText(graphics2D, menuBarFont, text);
        Integer y = gamePanel.tileSize * 6;
        super.drawText(graphics2D, menuBarFont, text, x, y);
        if (currentSelected == SelectedOption.PLAY) {
            super.drawText(graphics2D, menuBarFont, ">", x - gamePanel.tileSize, y);
        }

        text = "Sair";
        x = getCenterCoordinateForText(graphics2D, menuBarFont, text);
        y += gamePanel.tileSize;
        super.drawText(graphics2D, menuBarFont, text, x, y);
        if (currentSelected == SelectedOption.QUIT) {
            super.drawText(graphics2D, menuBarFont, ">", x - gamePanel.tileSize, y);
        }
    }
}
