package se.cygni.snake.api;

public class GameMessage {

    private String receivingPlayerId;
    private final String type = this.getClass().getCanonicalName();

    public String getType() {
        return type;
    }

    public String getReceivingPlayerId() {
        return receivingPlayerId;
    }

    public void setReceivingPlayerId(String receivingPlayerId) {
        this.receivingPlayerId = receivingPlayerId;
    }
}
