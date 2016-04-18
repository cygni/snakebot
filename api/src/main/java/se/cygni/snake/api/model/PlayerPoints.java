package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerPoints {

    private final String name;
    private final String playerId;
    private final int points;

    @JsonCreator
    public PlayerPoints(
            @JsonProperty("name") String name,
            @JsonProperty("playerId") String playerId,
            @JsonProperty("points") int points) {
        this.name = name;
        this.playerId = playerId;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getPoints() {
        return points;
    }
}
