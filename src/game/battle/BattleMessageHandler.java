package game.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleMessageHandler {

    public List<String> battleMessages;

    // Boolean para permitir o jogador ir para a próxima mensagem
    public boolean canProceed = false;

    // Variáveis de controle para animação do texto
    public int currentCharIndex = 0;
    public long lastUpdateTime = 0;
    public final long charDelay = 25;

    public BattleMessageHandler() {
        battleMessages = new ArrayList<String>();
    }

    public void resetMessageAnimation() {
        currentCharIndex = 0;
    }

    public void updateMessage() {
        if (this.getMessage() == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= charDelay) {
            lastUpdateTime = currentTime;

            // Avança o índice de caracteres até o tamanho máximo da mensagem
            if (currentCharIndex < this.getMessage().length()) {
                currentCharIndex++;
            }
        }

        // Verifica se a mensagem inteira ja teve animação
        updateCanProceed();
    }

    public void updateCanProceed() {
        this.canProceed = currentCharIndex == this.getMessage().length();
    }

    public void sendMessage(String message) {
        battleMessages.add(message);
    }

    public void sendMessage(String[] messages) {
        battleMessages.addAll(Arrays.asList(messages));
    }

    public String getMessage() {
        return battleMessages.get(0);
    }

    public void consumeMessage() {
        if (!battleMessages.isEmpty())
            battleMessages.remove(0);
    }

    public int size() {
        return battleMessages.size();
    }

    public boolean isEmpty() {
        return battleMessages.isEmpty();
    }
}
