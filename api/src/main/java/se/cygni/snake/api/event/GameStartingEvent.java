package se.cygni.snake.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class GameStartingEvent extends GameMessage {

    private final String gameId;
    private final int noofPlayers;
    private final int width;
    private final int height;
    private final GameSettings gameSettings;

    @JsonCreator
    public GameStartingEvent(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("noofPlayers") int noofPlayers,
            @JsonProperty("width") int width,
            @JsonProperty("height") int gameHeight,
            @JsonProperty("gameSettings") GameSettings gameSettings) {

        this.gameId = gameId;
        this.noofPlayers = noofPlayers;
        this.width = width;
        this.height = gameHeight;
        this.gameSettings = gameSettings;
    }

    public GameStartingEvent(GameStartingEvent gse) {
        this.gameId = gse.getGameId();
        this.noofPlayers = gse.getNoofPlayers();
        this.width = gse.getWidth();
        this.height = gse.getHeight();
        this.gameSettings = gse.gameSettings;
    }

    public String getGameId() {
        return gameId;
    }

    public int getNoofPlayers() {
        return noofPlayers;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    @Override
    public String toString() {
        return "GameStartingEvent{" +
                "gameId='" + gameId + '\'' +
                ", noofPlayers=" + noofPlayers +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
