package se.cygni.snake.websocket.event.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.game.GameFeatures;

import java.util.List;

public class ActiveGame {
    public final String gameId;
    public final GameFeatures gameFeatures;
    public final List<ActiveGamePlayer> players;

    @JsonCreator
    public ActiveGame(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("gameFeature") GameFeatures gameFeatures,
            @JsonProperty("players") List<ActiveGamePlayer> players) {
        this.gameId = gameId;
        this.gameFeatures = gameFeatures;
        this.players = players;
    }
}