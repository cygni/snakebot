package se.cygni.snake.eventapi.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class GameHistorySearchItem {

    private String gameId;
    private String[] players;
    private LocalDateTime gameDate;

    public GameHistorySearchItem() {
    }

    public GameHistorySearchItem(String gameId, String[] players, LocalDateTime gameDate) {
        this.gameId = gameId;
        this.players = players;
        this.gameDate = gameDate;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalDateTime getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDateTime gameDate) {
        this.gameDate = gameDate;
    }
}
