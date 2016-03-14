package se.cygni.snake.websocket.event.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveGamePlayer {
    public final String name;
    public final String id;

    @JsonCreator
    public ActiveGamePlayer(
            @JsonProperty("name") String name,
            @JsonProperty("id") String id) {
        this.name = name;
        this.id = id;
    }
}