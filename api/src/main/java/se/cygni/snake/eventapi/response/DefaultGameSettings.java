package se.cygni.snake.eventapi.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class DefaultGameSettings extends ApiMessage {

    private final GameSettings gameSettings;

    @JsonCreator
    public DefaultGameSettings(
            @JsonProperty("gameSettings") GameSettings gameSettings) {

        this.gameSettings = gameSettings;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }
}
