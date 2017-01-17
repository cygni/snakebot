package se.cygni.snake.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

import java.util.List;
import java.util.Map;

@GameMessageType
public class ArenaUpdateEvent extends GameMessage {
    private final String arenaName;
    private final String gameId;
    private final Boolean ranked;
    private final List<String> onlinePlayers;
    private final Map<String, Long> rating;
    private final List<ArenaHistory> gameHistory;

    public static class ArenaHistory {
        public ArenaHistory(String gameId, List<String> playerPositions) {
            this.gameId = gameId;
            this.playerPositions = playerPositions;
        }

        private final String gameId;
        private final List<String> playerPositions;

        public String getGameId() {
            return gameId;
        }

        public List<String> getPlayerPositions() {
            return playerPositions;
        }
    }

    @JsonCreator
    public ArenaUpdateEvent(
            @JsonProperty("arenaName") String arenaName,
            @JsonProperty("gameId") String gameId,
            @JsonProperty("ranked") Boolean ranked,
            @JsonProperty("rating") Map<String, Long> rating,
            @JsonProperty("onlinePlayers") List<String> onlinePlayers,
            @JsonProperty("gameHistory") List<ArenaHistory> gameHistory) {

        this.arenaName = arenaName;
        this.gameId = gameId;
        this.ranked = ranked;
        this.rating = rating;
        this.onlinePlayers = onlinePlayers;
        this.gameHistory = gameHistory;
    }

    public ArenaUpdateEvent(ArenaUpdateEvent other) {
        this.arenaName = other.getArenaName();
        this.gameId = other.getGameId();
        this.ranked = other.ranked;
        this.rating = other.rating;
        this.gameHistory = other.gameHistory;

        this.onlinePlayers = other.getOnlinePlayers();
    }

    public String getArenaName() {
        return arenaName;
    }

    public String getGameId() {
        return gameId;
    }

    public Boolean getRanked() {
        return ranked;
    }

    public Map<String, Long> getRating() {
        return rating;
    }

    public List<String> getOnlinePlayers() {
        return onlinePlayers;
    }

    public List<ArenaHistory> getGameHistory() {
        return gameHistory;
    }
}