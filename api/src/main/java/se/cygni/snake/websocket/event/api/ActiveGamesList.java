package se.cygni.snake.websocket.event.api;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.websocket.event.api.type.ApiMessageType;

import java.util.List;

@ApiMessageType
public class ActiveGamesList extends ApiMessage {

    public final List<ActiveGame> games;

    @JsonCreator
    public ActiveGamesList(
            @JsonProperty("games") List<ActiveGame> games) {
        this.games = games;
    }
}
