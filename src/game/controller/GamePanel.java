package game.controller;

import ui.BattleMenuUI;
import game.states.GameState;
import game.input.KeyHandler;
import ui.MainMenuUI;
import utils.StatusSprites;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    public final StatusSprites statusSprites = new StatusSprites();

    // Configurações da Tela
    public final int originalTileSize = 16; // 16x16 tile
    public final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    public int FPS = 60;

    public Thread gameThread;

    public KeyHandler keyHandler = new KeyHandler(this);
    public GameState gameState = GameState.TITLE_STATE;

    // UIs
    public MainMenuUI mainMenuUI = new MainMenuUI(this);
    public BattleMenuUI battleMenuUI = new BattleMenuUI(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread.isAlive()) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        draw(graphics2D);
        graphics2D.dispose();
    }

    // Atualiza as informações
    public void update() {
        switch(gameState) {
            case TITLE_STATE:
                mainMenuUI.update();
                break;

            case PLAY_STATE:
                battleMenuUI.update();
                break;

            case EXIT_STATE:
                System.exit(0);
                break;
        }
    }

    // Desenha os elementos na tela
    public void draw(Graphics2D g2d) {
        switch(gameState) {
            case TITLE_STATE:
                mainMenuUI.draw(g2d);
                break;

            case PLAY_STATE:
                battleMenuUI.draw(g2d);
                break;
        }
    }
}
