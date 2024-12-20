package game.message;

public class BattleMessage {
    private String message;
    private BattleMessageType type;
    private int target;

    public BattleMessage(String message, BattleMessageType type) {
        this.message = message;
        this.type = type;
        this.target = 1;
    }

    public BattleMessage(String message, BattleMessageType type, int target) {
        this.message = message;
        this.type = type;
        this.target = target;
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

    @Override
    public String toString() {
        return "BattleMessage{" +
                "message='" + message + '\'' +
                ", type=" + type +
                ", target=" + target +
                '}';
    }
}
