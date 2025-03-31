package game;

import game.controller.GamePanel;

import javax.swing.*;

public class Game {

    private final String gameTitle;

    public Game(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public void start() {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle(this.gameTitle);

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}
