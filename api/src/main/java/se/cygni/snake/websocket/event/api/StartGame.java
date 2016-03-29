package se.cygni.snake.websocket.event.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.websocket.event.api.type.ApiMessageType;

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
