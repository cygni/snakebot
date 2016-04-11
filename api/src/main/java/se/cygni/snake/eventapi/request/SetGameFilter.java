package se.cygni.snake.eventapi.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

@ApiMessageType
public class SetGameFilter extends ApiMessage {

    private final String[] includedGameIds;

    @JsonCreator
    public SetGameFilter(
            @JsonProperty("includedGameIds") String[] includedGameIds) {
        this.includedGameIds = includedGameIds;
    }

    public String[] getIncludedGameIds() {
        return includedGameIds;
    }
}
