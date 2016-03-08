package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MapSnakeHead implements TileContent {

    public static final String CONTENT = "snakehead";

    final String name;
    final String playerId;

    @JsonCreator
    public MapSnakeHead(
            @JsonProperty("name") String name,
            @JsonProperty("playerId") String playerId)
    {
        this.name = name;
        this.playerId = playerId;
    }

    @Override
    public String getContent() {
        return CONTENT;
    }

    public String getName() {
        return name;
    }

    public String getPlayerId() {
        return playerId;
    }

    @JsonIgnore
    public String toDisplay() {
        return "SH";
    }
}
