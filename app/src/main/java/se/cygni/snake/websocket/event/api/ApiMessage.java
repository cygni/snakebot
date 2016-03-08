package se.cygni.snake.websocket.event.api;

public class ApiMessage {

    private final String type = this.getClass().getCanonicalName();

    public String getType() {
        return type;
    }

}
