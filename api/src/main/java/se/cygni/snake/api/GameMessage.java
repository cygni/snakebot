package se.cygni.snake.api;

public class GameMessage {

    private String receivingPlayerId;
    private final String type = this.getClass().getCanonicalName();
    private final String simpleType = this.getClass().getSimpleName();

    public String getType() {
        return type;
    }

    public String getSimpleType() {
        return simpleType;
    }

    public String getReceivingPlayerId() {
        return receivingPlayerId;
    }

    public void setReceivingPlayerId(String receivingPlayerId) {
        this.receivingPlayerId = receivingPlayerId;
    }
}
