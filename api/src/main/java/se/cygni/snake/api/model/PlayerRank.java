package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerRank {

    public final String playerName;
    public final String playerId;
    public final int rank;
    public final int points;
    public final boolean alive;

    @JsonCreator
    public PlayerRank(
            @JsonProperty("playerName") String playerName,
            @JsonProperty("playerId") String playerId,
            @JsonProperty("rank") int rank,
            @JsonProperty("points") int points,
            @JsonProperty("alive") boolean alive) {

        this.playerName = playerName;
        this.playerId = playerId;
        this.rank = rank;
        this.points = points;
        this.alive = alive;
    }

    @Override
    public String toString() {
        return rank + ".\t" + points + " pts\t" + playerName + " (" + (alive ? "alive" : "dead") + ")";
    }
}
