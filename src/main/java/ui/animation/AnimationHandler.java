package ui.animation;

import game.controller.GamePanel;
import ui.sprite.SpritesHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AnimationHandler {

    GamePanel gamePanel;
    Color backgroundColor = new Color(246, 167, 36);
    SpritesHandler spritesHandler;

    int botMenuX = 336;
    int botMenuY = 48;
    int playerMenuX = 48;
    int playerMenuY = 240;

    int margin = 5;

    int transitionAnimationCounter = 0;
    public boolean transitionAnimation = false;

    int target;

    // Player UI's
    int damageAnimationCounter = 0;
    int healAnimationCounter = 0;
    int manaAnimationCounter = 0;
    int powerUpAnimationCounter = 0;
    int elementalPowerUpOrDownAnimation = 0;
    boolean damageAnimation = false;
    boolean healAnimation = false;
    boolean manaAnimation = false;
    boolean playerPowerUpAnimation = false;
    boolean botPowerUpAnimation = false;
    boolean elementalPowerUpAnimation = false;
    boolean elementalPowerDownAnimation = false;

    int totalSprites = 5;
    int animationSpeed = 30;

    public AnimationHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.spritesHandler = gamePanel.spritesHandler;
    }

    public void draw(Graphics2D g2d) {
        if (transitionAnimation) {
            drawTransition(g2d);
        }
        if (damageAnimation) {
            drawDamageAnimation(g2d);
        }
        if (healAnimation) {
            drawHealAnimation(g2d);
        }
        if (manaAnimation) {
            drawManaAnimation(g2d);
        }
        if (playerPowerUpAnimation) {
            drawPlayerPowerUpAnimation(g2d);
        }
        if (botPowerUpAnimation) {
            drawBotPowerUpAnimation(g2d);
        }
        if (elementalPowerUpAnimation || elementalPowerDownAnimation) {
            drawElementalPowerUpOrDownAnimation(g2d);
        }
    }

    public void enableElementalPowerUpAnimation() {
        elementalPowerUpAnimation = true;
    }

    public void enableElementalPowerDownAnimation() {
        elementalPowerDownAnimation = true;
    }

    public void enablePlayerPowerUpAnimation() {
        playerPowerUpAnimation = true;
    }

    public void enableBotPowerUpAnimation() {
        botPowerUpAnimation = true;
    }

    public void disablePowerUpAnimation() {
        playerPowerUpAnimation = false;
        botPowerUpAnimation = false;
    }

    public void enableDamageAnimation(int target) {
        this.target = target;
        damageAnimation = true;
    }

    public void enableHealAnimation(int target) {
        this.target = target;
        healAnimation = true;
    }

    public void enableManaAnimation(int target) {
        this.target = target;
        manaAnimation = true;
    }

    public void drawElementalPowerUpOrDownAnimation(Graphics2D g2d) {
        elementalPowerUpOrDownAnimation++;

        // Calcula as coordenadas
        int x = playerMenuX + 4 * gamePanel.tileSize - 8 + 3*margin;
        int y = playerMenuY + 2 * gamePanel.tileSize - 10;

        // Recupera o sprite do power-up
        BufferedImage elementalPowerUpSprite = elementalPowerUpAnimation ? spritesHandler.getElementalPowerUp() : spritesHandler.getElementalPowerDown();

        // Define a duração total da animação em frames
        int animationDuration = 120;
        float alpha = opacityProgress(animationDuration);

        if (alpha > 0) {
            // Configura a opacidade
            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2d.setComposite(composite);

            // Desenha o sprite com a opacidade calculada
            g2d.drawImage(elementalPowerUpSprite, x, y, gamePanel.tileSize / 2, gamePanel.tileSize / 2, null);

            // Restaura a opacidade padrão
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }


        // Reinicia o contador após a animação
        if (elementalPowerUpOrDownAnimation >= animationDuration) {
            elementalPowerUpOrDownAnimation = 0; // Ou marque como concluído, se necessário
            elementalPowerUpAnimation = false;
            elementalPowerDownAnimation = false;
        }
    }

    private float opacityProgress(int animationDuration) {
        int halfDuration = animationDuration / 2;

        // Calcula a opacidade com base no progresso da animação
        float alpha;
        if (elementalPowerUpOrDownAnimation <= halfDuration) {
            // Aumenta a opacidade de 0 a 1 na primeira metade
            alpha = (float) elementalPowerUpOrDownAnimation / halfDuration;
        } else {
            // Diminui a opacidade de 1 a 0 na segunda metade
            alpha = 1.0f - (float) (elementalPowerUpOrDownAnimation - halfDuration) / halfDuration;
        }
        return alpha;
    }

    public void drawPowerUpAnimation(Graphics2D g2d, int x, int y) {

        // Caso os dois estejam ativos o contador iria incrementar 2
        // portando verificamos antes e incrementamos conforme os aprimoramentos ativos
        powerUpAnimationCounter = (playerPowerUpAnimation && botPowerUpAnimation ?
                powerUpAnimationCounter + 1 : powerUpAnimationCounter + 2);

        BufferedImage powerUpSprite = spritesHandler.getPowerUp();

        // Define a duração de cada estado (visível/invisível) em frames
        int blinkDuration = 60;

        // Determina a opacidade com base no contador
        boolean isVisible = (powerUpAnimationCounter / blinkDuration) % 2 == 0;

        if (isVisible) {
            // Desenha o sprite com opacidade total
            g2d.drawImage(powerUpSprite, x, y, gamePanel.tileSize / 2, gamePanel.tileSize / 2, null);
        }

        // Reinicia o contador para evitar overflow
        if (powerUpAnimationCounter >= blinkDuration * 2) {
            powerUpAnimationCounter = 0;
        }
    }

    public void drawPlayerPowerUpAnimation(Graphics2D g2d) {
        int x = playerMenuX + margin;
        int y = playerMenuY + margin;

        drawPowerUpAnimation(g2d, x, y);
    }

    public void drawBotPowerUpAnimation(Graphics2D g2d) {
        int x = botMenuX + margin;
        int y = botMenuY + margin;

        drawPowerUpAnimation(g2d, x, y);
    }

    public void drawDamageAnimation(Graphics2D g2d) {
        damageAnimationCounter++;
        if (damageAnimationCounter >= animationSpeed) {
            damageAnimationCounter = 0;
            damageAnimation = false;
        }

        int x = target == 1 ?  playerMenuX : botMenuX;
        int y = target == 1 ? playerMenuY : botMenuY;

        BufferedImage characterSprite = switch (target) {
          case 1 -> spritesHandler.getPlayerDamage();
          default -> spritesHandler.getBotDamage();
        };

        g2d.drawImage(characterSprite, x + margin, y + margin, 2*gamePanel.tileSize, 2*gamePanel.tileSize, null);
    }

    public  void drawHealAnimation(Graphics2D g2d) {
        int currentSpriteIndex = healAnimationCounter / (animationSpeed / totalSprites);

        if (currentSpriteIndex >= totalSprites) {
            healAnimationCounter = 0; // Reseta o contador para futuras animações
            healAnimation = false;    // Finaliza a animação
            return;                   // Interrompe o desenho após o último sprite
        }

        // Calcula as coordenadas com base no alvo (jogador ou bot)
        int x = target == 1 ? playerMenuX : botMenuX;
        int y = target == 1 ? playerMenuY : botMenuY;

        // Obtém o sprite correspondendo ao índice atual
        BufferedImage characterSprite = target == 1
                ? switch (currentSpriteIndex) {
            case 0 -> spritesHandler.getPlayerHeal0();
            case 1 -> spritesHandler.getPlayerHeal1();
            case 2 -> spritesHandler.getPlayerHeal2();
            case 3 -> spritesHandler.getPlayerHeal3();
            case 4 -> spritesHandler.getPlayerHeal4();
            default -> null; // Fallback, nunca deve ocorrer
        }
                : switch (currentSpriteIndex) {
            case 0 -> spritesHandler.getBotHeal0();
            case 1 -> spritesHandler.getBotHeal1();
            case 2 -> spritesHandler.getBotHeal2();
            case 3 -> spritesHandler.getBotHeal3();
            case 4 -> spritesHandler.getBotHeal4();
            default -> null; // Fallback, nunca deve ocorrer
        };

        if (characterSprite != null) {
            // Desenha o sprite na posição calculada
            g2d.drawImage(characterSprite, x + margin, y + margin, 2 * gamePanel.tileSize, 2 * gamePanel.tileSize, null);
        }

        // Incrementa o contador geral da animação
        healAnimationCounter++;
    }

    public void drawManaAnimation(Graphics2D g2d) {
        int currentSpriteIndex = manaAnimationCounter / (animationSpeed / totalSprites);

        if (currentSpriteIndex >= totalSprites) {
            manaAnimationCounter = 0; // Reseta o contador para futuras animações
            manaAnimation = false;    // Finaliza a animação
            return;                   // Interrompe o desenho após o último sprite
        }

        // Calcula as coordenadas com base no alvo (jogador ou bot)
        int x = target == 1 ? playerMenuX : botMenuX;
        int y = target == 1 ? playerMenuY : botMenuY;

        // Obtém o sprite correspondendo ao índice atual
        BufferedImage characterSprite = target == 1
                ? switch (currentSpriteIndex) {
            case 0 -> spritesHandler.getPlayerMana0();
            case 1 -> spritesHandler.getPlayerMana1();
            case 2 -> spritesHandler.getPlayerMana2();
            case 3 -> spritesHandler.getPlayerMana3();
            case 4 -> spritesHandler.getPlayerMana4();
            default -> null; // Fallback, nunca deve ocorrer
        }
                : switch (currentSpriteIndex) {
            case 0 -> spritesHandler.getBotMana0();
            case 1 -> spritesHandler.getBotMana1();
            case 2 -> spritesHandler.getBotMana2();
            case 3 -> spritesHandler.getBotMana3();
            case 4 -> spritesHandler.getBotMana4();
            default -> null; // Fallback, nunca deve ocorrer
        };

        if (characterSprite != null) {
            // Desenha o sprite na posição calculada
            g2d.drawImage(characterSprite, x + margin, y + margin, 2 * gamePanel.tileSize, 2 * gamePanel.tileSize, null);
        }

        // Incrementa o contador geral da animação
        manaAnimationCounter++;
    }

    public void drawTransition(Graphics2D g2d) {
        // Obtém o tamanho da tela
        int screenWidth = gamePanel.getWidth();
        int screenHeight = gamePanel.getHeight();

        // Calcula o tamanho atual do círculo em expansão (diâmetro)
        int currentSize = transitionAnimationCounter;

        // Calcula as coordenadas do círculo para mantê-lo centralizado
        int circleX = (screenWidth / 2) - (currentSize / 2);
        int circleY = (screenHeight / 2) - (currentSize / 2);

        // Configura a cor
        g2d.setColor(backgroundColor);

        // Desenha o círculo que está expandindo
        g2d.fillOval(circleX, circleY, currentSize, currentSize);

        // Incrementa o contador para aumentar o tamanho do círculo
        transitionAnimationCounter += 30;

        // Quando o círculo cobre mais que a tela (150% da maior dimensão), finaliza a animação
        if (currentSize >= 1.5 * Math.max(screenWidth, screenHeight)) {
            transitionAnimation = false; // Finaliza a animação
            transitionAnimationCounter = 0; // Reseta o contador para futuras animações
        }
    }
}
