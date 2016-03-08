package se.cygni.snake.websocket.event.api;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.websocket.event.api.type.ApiMessageType;

@ApiMessageType
public class ActiveGamesList extends ApiMessage {

    private String[] activeGameIds;

    @JsonCreator
    public ActiveGamesList(
            @JsonProperty("activeGameIds") String[] activeGameIds) {
        this.activeGameIds = activeGameIds;
    }

    public String[] getActiveGameIds() {
        return activeGameIds;
    }
}
