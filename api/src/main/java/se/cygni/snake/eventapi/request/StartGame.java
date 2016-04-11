package se.cygni.snake.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class StartGame extends ApiMessage {

    private final String gameId;

    @JsonCreator
    public StartGame(
            @JsonProperty("gameId") String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
