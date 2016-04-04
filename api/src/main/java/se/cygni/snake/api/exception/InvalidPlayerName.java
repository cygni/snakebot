package se.cygni.snake.api.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class InvalidPlayerName extends GameMessage {

    public enum PlayerNameInvalidReason {
        Taken,
        Empty,
        InvalidCharacter
    }

    private PlayerNameInvalidReason reasonCode;

    @JsonCreator
    public InvalidPlayerName(
            @JsonProperty("PlayerNameInvalidReason") PlayerNameInvalidReason reasonCode) {
        this.reasonCode = reasonCode;
    }

    public PlayerNameInvalidReason getReasonCode() {
        return reasonCode;
    }
}
