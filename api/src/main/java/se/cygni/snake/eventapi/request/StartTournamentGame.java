package se.cygni.snake.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class StartTournamentGame extends ApiMessage {

    private final String token;
    private final String gameId;

    @JsonCreator
    public StartTournamentGame(
            @JsonProperty("token") String token,
            @JsonProperty("gameId") String gameId) {

        this.token = token;
        this.gameId = gameId;
    }

    public String getToken() {
        return token;
    }

    public String getGameId() {
        return gameId;
    }
}
