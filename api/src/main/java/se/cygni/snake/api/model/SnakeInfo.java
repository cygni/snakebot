package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SnakeInfo {

    final String name;
    final int length;
    final String id;
    final int x;
    final int y;

    @JsonCreator
    public SnakeInfo(
            @JsonProperty("name") String name,
            @JsonProperty("length") int length,
            @JsonProperty("playerId")String playerId,
            @JsonProperty("x")int x,
            @JsonProperty("y")int y
    )
    {

        this.name = name;
        this.length = length;
        this.id = playerId;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
