package se.cygni.snake.api;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
public abstract class GameMessage implements Cloneable {

    private String receivingPlayerId;
    private long timestamp = System.currentTimeMillis();

    public String getReceivingPlayerId() {
        return receivingPlayerId;
    }

    public void setReceivingPlayerId(String receivingPlayerId) {
        this.receivingPlayerId = receivingPlayerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
