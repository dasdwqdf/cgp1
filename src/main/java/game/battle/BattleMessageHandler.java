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

    public BattleMessageHandler() {
        battleMessages = new ArrayList<String>();
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
