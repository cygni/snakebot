package se.cygni.snake.api.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class PlayerRegistered extends GameMessage {

    private final String gameId;
    private final String name;
    private final String color;
    private final GameSettings gameSettings;
    private final GameMode gameMode;

    @JsonCreator
    public PlayerRegistered(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("name") String name,
            @JsonProperty("color") String color,
            @JsonProperty("gameSettings") GameSettings gameSettings,
            @JsonProperty("gameMode") GameMode gameMode) {

        this.gameId = gameId;
        this.name = name;
        this.color = color;
        this.gameSettings = gameSettings;
        this.gameMode = gameMode;
    }

    public String getGameId() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public String toString() {
        return "PlayerRegistered{" +
                "gameId='" + gameId + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", gameSettings=" + gameSettings +
                ", gameMode=" + gameMode +
                '}';
    }
}
