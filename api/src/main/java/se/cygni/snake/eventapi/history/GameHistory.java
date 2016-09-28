package se.cygni.snake.eventapi.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

import java.time.LocalDateTime;
import java.util.List;

@ApiMessageType
public class GameHistory extends ApiMessage {

    private final String gameId;

    private final String[] playerNames;

    private final LocalDateTime gameDate;

    private final List<GameMessage> messages;

    @JsonCreator
    public GameHistory(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("playerNames") String[] playerNames,
            @JsonProperty("gameDate") LocalDateTime gameDate,
            @JsonProperty("messages") List<GameMessage> messages) {
        this.gameId = gameId;
        this.playerNames = playerNames;
        this.gameDate = gameDate;
        this.messages = messages;
    }

    public String getGameId() {
        return gameId;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalDateTime getGameDate() {
        return gameDate;
    }

    public List<GameMessage> getMessages() {
        return messages;
    }
}
