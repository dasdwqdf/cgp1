package game.message;

import game.entity.PlayerEntity;
import game.view.PlayerView;

public class BattleMessage {
    private String message;
    private BattleMessageType type;
    private int target;
    private PlayerView playerSnapshot;

    public BattleMessage(String message, BattleMessageType type) {
        this.message = message;
        this.type = type;
        this.target = 1;
        this.playerSnapshot = null;
    }

    public BattleMessage(String message, BattleMessageType type, int target) {
        this.message = message;
        this.type = type;
        this.target = target;
        this.playerSnapshot = null;
    }

    public BattleMessage(String message, BattleMessageType type, int target, PlayerView playerSnapshot) {
        this.message = message;
        this.type = type;
        this.target = target;
        this.playerSnapshot = playerSnapshot;
    }

    public String getMessage() {
        return message;
    }

    public BattleMessageType getType() {
        return type;
    }

    public int getTarget() {
        return target;
    }

    public PlayerView getPlayerSnapshot() {
        return playerSnapshot;
    }

    @Override
    public String toString() {
        return "BattleMessage{" +
                "message='" + message + '\'' +
                ", type=" + type +
                ", target=" + target +
                ", playerSnapshot=" + playerSnapshot +
                '}';
    }
}
