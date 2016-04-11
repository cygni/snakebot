package se.cygni.snake.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class UpdateTournamentSettings extends ApiMessage {

    private final String token;
    private final GameSettings gameSettings;

    @JsonCreator
    public UpdateTournamentSettings(
            @JsonProperty("token") String token,
            @JsonProperty("gameSettings") GameSettings gameSettings) {

        this.token = token;
        this.gameSettings = gameSettings;
    }

    public String getToken() {
        return token;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }
}
