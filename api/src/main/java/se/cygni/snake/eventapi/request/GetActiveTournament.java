package se.cygni.snake.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class GetActiveTournament extends ApiMessage {

    private final String token;

    @JsonCreator
    public GetActiveTournament(
            @JsonProperty("token") String token) {

        this.token = token;
    }

    public String getToken() {
        return token;
    }

   }

