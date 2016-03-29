package se.cygni.snake.websocket.event.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.model.GameSettings;

import java.util.List;

public class ActiveGame {
    public final String gameId;
    public final boolean subscribing;
    public final GameSettings gameFeatures;
    public final List<ActiveGamePlayer> players;


    @JsonCreator
    public ActiveGame(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("subscribing") boolean subscribing,
            @JsonProperty("gameFeature") GameSettings gameFeatures,
            @JsonProperty("players") List<ActiveGamePlayer> players) {
        this.gameId = gameId;
        this.subscribing = subscribing;
        this.gameFeatures = gameFeatures;
        this.players = players;
    }
}