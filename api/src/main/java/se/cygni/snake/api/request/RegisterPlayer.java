package se.cygni.snake.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class RegisterPlayer extends GameMessage {

    private final String playerName;
    private final GameSettings gameSettings;

    public RegisterPlayer(
            String playerName) {

        this.playerName = playerName;
        this.gameSettings = null;
    }

    @JsonCreator
    public RegisterPlayer(
            @JsonProperty("playerName") String playerName,
            @JsonProperty("gameSettings") GameSettings gameSettings) {

        this.playerName = playerName;
        this.gameSettings = gameSettings;
    }

    public String getPlayerName() {
        return playerName;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }
}