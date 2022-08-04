package se.cygni.snake.api.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class ArenaIsFull extends GameMessage {

    private int playersConnected;

    @JsonCreator
    public ArenaIsFull(
            @JsonProperty("playersConnected") int playersConnected) {
        this.playersConnected = playersConnected;
    }

    public int getPlayersConnected() {
        return playersConnected;
    }
}
