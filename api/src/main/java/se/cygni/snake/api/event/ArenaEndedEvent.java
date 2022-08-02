package se.cygni.snake.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class ArenaEndedEvent extends GameMessage {
    private final String arenaName;

    @JsonCreator
    public ArenaEndedEvent(
            @JsonProperty("arenaName") String arenaName) {

        this.arenaName = arenaName;
    }

    public String getArenaName() {
        return arenaName;
    }
}
