package se.cygni.snake.history.repository;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import se.cygni.snake.api.GameMessage;

import java.time.LocalDateTime;
import java.util.List;

@Document
public class GameHistory {

    @Id
    private final String gameId;

    private String[] playerNames;

    private LocalDateTime gameDate;

    private List<GameMessage> messages;

    public GameHistory(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(String[] playerNames) {
        this.playerNames = playerNames;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalDateTime getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDateTime gameDate) {
        this.gameDate = gameDate;
    }

    public List<GameMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<GameMessage> messages) {
        this.messages = messages;
    }
}
