package se.cygni.snake.api.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class InvalidArenaName extends GameMessage {

    public enum ArenaNameInvalidReason {
        Nonexistent,
    }

    private ArenaNameInvalidReason reasonCode;

    @JsonCreator
    public InvalidArenaName(
            @JsonProperty("ArenaNameInvalidReason") ArenaNameInvalidReason reasonCode) {
        this.reasonCode = reasonCode;
    }

    public ArenaNameInvalidReason getReasonCode() {
        return reasonCode;
    }

}
