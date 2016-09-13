package se.cygni.snake.api;

public class GameMessage implements Cloneable {

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
