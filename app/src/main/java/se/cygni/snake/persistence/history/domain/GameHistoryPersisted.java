package se.cygni.snake.persistence.history.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.type.ApiMessageType;

import java.time.LocalDateTime;

@ApiMessageType
public class GameHistoryPersisted extends ApiMessage {

    private final String gameId;

    private final String[] playerNames;

    private final LocalDateTime gameDate;

    @JsonCreator
    public GameHistoryPersisted(
            @JsonProperty("gameId") String gameId,
            @JsonProperty("playerNames") String[] playerNames,
            @JsonProperty("gameDate") LocalDateTime gameDate) {

        this.gameId = gameId;
        this.playerNames = playerNames;
        this.gameDate = gameDate;
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
}
