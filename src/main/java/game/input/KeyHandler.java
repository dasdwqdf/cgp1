package game.input;

import game.controller.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public GamePanel gamePanel;
    public boolean enterPressed;
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean zPressed, xPressed;

    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    private void handleInputBattleUI(int code) {
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            rightPressed = true;
        }

        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            leftPressed = true;
        }

        if (code == KeyEvent.VK_Z) {
            zPressed = true;
        }

        if (code == KeyEvent.VK_X) {
            xPressed = true;
        }

        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
    }

    private void handleInputTitleUI(int code) {
       if (code == KeyEvent.VK_ENTER) {
           enterPressed = true;
       }

       if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
           upPressed = true;
       }

       if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
           downPressed = true;
       }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        switch (gamePanel.gameState) {
            case TITLE_STATE -> handleInputTitleUI(code);
            case PLAY_STATE -> handleInputBattleUI(code);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
