package game.message;

import java.util.ArrayList;
import java.util.List;

public class NewBattleMessageHandler {

    List<BattleMessage> battleMessageList;

    public NewBattleMessageHandler() {
        battleMessageList = new ArrayList<>();
    }

    public boolean isBattleMessageListEmpty() {
        return battleMessageList.isEmpty();
    }

    public void addMessage(BattleMessage battleMessage) {
        battleMessageList.add(battleMessage);
    }

    public String getBattleMessage() {
        if (battleMessageList.isEmpty()) {
            return null;
        }

        BattleMessage battleMessage = battleMessageList.get(0);
        return battleMessage.getMessage();
    }

    public BattleMessage consumeMessage() {
        if (battleMessageList.isEmpty()) {
            return null;
        }

        // Removemos o primeiro elemento da lista
        return battleMessageList.remove(0);
    }
}
