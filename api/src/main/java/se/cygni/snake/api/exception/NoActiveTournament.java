package se.cygni.snake.api.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class NoActiveTournament extends GameMessage {

    @JsonCreator
    public NoActiveTournament() {
    }

}
