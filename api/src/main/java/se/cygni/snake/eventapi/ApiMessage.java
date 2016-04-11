package se.cygni.snake.eventapi;

public class ApiMessage {

    private final String type = this.getClass().getCanonicalName();

    public String getType() {
        return type;
    }

}
