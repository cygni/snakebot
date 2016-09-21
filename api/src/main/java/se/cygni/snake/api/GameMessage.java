package se.cygni.snake.api;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
public abstract class GameMessage implements Cloneable {

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
