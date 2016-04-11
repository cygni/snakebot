package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SnakeInfo {

    final String name;
    final int points;
    final String id;
    final int[] positions;
    final int tailProtectedForGameTicks;

    @JsonCreator
    public SnakeInfo(
            @JsonProperty("name") String name,
            @JsonProperty("points") int points,
            @JsonProperty("playerId")String playerId,
            @JsonProperty("positions") int[] positions,
            @JsonProperty("tailProtectedForGameTicks") int tailProtectedForGameTicks
    )
    {
        this.name = name;
        this.points = points;
        this.id = playerId;
        this.positions = positions;
        this.tailProtectedForGameTicks = tailProtectedForGameTicks;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public int getLength() {
        return positions.length;
    }

    public int getPoints() {
        return points;
    }

    public String getId() {
        return id;
    }

    public int getTailProtectedForGameTicks() {
        return tailProtectedForGameTicks;
    }

    @JsonIgnore
    public boolean isAlive() {
        return getLength() > 0;
    }

    public int[] getPositions() {
        return positions;
    }
}
