package se.cygni.snake.eventapi.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class InvalidArenaName extends ApiMessage {

    @JsonCreator
    public InvalidArenaName() {}

}
