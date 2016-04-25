package se.cygni.snake.eventapi.response;


import com.fasterxml.jackson.annotation.JsonCreator;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class NoActiveTournamentEvent extends ApiMessage {

    @JsonCreator
    public NoActiveTournamentEvent(){}
}
