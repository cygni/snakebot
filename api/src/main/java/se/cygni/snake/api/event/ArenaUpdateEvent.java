package se.cygni.snake.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

import java.util.List;

@GameMessageType
public class ArenaUpdateEvent extends GameMessage {
    private final String arenaName;
    private final String gameId;
    private final List<String> onlinePlayers;
    // private final Rankings rankings;

    @JsonCreator
    public ArenaUpdateEvent(
            @JsonProperty("arenaName") String arenaName,
            @JsonProperty("gameId") String gameId,
            @JsonProperty("onlinePlayers") List<String> onlinePlayers) {

        this.arenaName = arenaName;
        this.gameId = gameId;
        this.onlinePlayers = onlinePlayers;
    }

    public ArenaUpdateEvent(ArenaUpdateEvent other) {
        this.arenaName = other.getArenaName();
        this.gameId = other.getGameId();
        this.onlinePlayers = other.getOnlinePlayers();
    }

    public String getArenaName() {
        return arenaName;
    }

    public String getGameId() {
        return gameId;
    }

    public List<String> getOnlinePlayers() {
        return onlinePlayers;
    }
}