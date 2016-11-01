package se.cygni.snake.eventapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveGamePlayer {
    public final String name;
    public final String id;
    public final int points;
    public final boolean isWinner;
    public final boolean isMovedUpInTournament;

    @JsonCreator
    public ActiveGamePlayer(
            @JsonProperty("name") String name,
            @JsonProperty("id") String id,
            @JsonProperty("points") int points
    ) {
        this.name = name;
        this.id = id;
        this.points = points;
        this.isWinner = false;
        this.isMovedUpInTournament = false;
    }

    @JsonCreator
    public ActiveGamePlayer(
            @JsonProperty("name") String name,
            @JsonProperty("id") String id,
            @JsonProperty("points") int points,
            @JsonProperty("isWinner") boolean isWinner,
            @JsonProperty("isMovedUpInTournament") boolean isMovedUpInTournament
    ) {
        this.name = name;
        this.id = id;
        this.points = points;
        this.isWinner = isWinner;
        this.isMovedUpInTournament = isMovedUpInTournament;
    }
}