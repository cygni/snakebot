package se.cygni.snake.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class SetCurrentArena extends ApiMessage {
    private final String currentArena;

    @JsonCreator
    public SetCurrentArena(
            @JsonProperty("currentArena") String currentArena) {
        this.currentArena = currentArena;
    }

    public String getCurrentArena() {
        return currentArena;
    }
}
