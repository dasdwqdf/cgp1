package game.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleMessageHandler {

    public List<String> battleMessages;

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
