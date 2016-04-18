package se.cygni.snake.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class RegisterMove extends GameMessage {

    private final long gameTick;
    private final String gameId;
    private final SnakeDirection direction;

    @JsonCreator
    public RegisterMove(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("gameTick") long gameTick,
            @JsonProperty("direction") SnakeDirection direction) {

        this.gameId = gameId;
        this.gameTick = gameTick;
        this.direction = direction;
    }

    public String getGameId() {
        return gameId;
    }

    public long getGameTick() {
        return gameTick;
    }

    public SnakeDirection getDirection() {
        return direction;
    }
}
