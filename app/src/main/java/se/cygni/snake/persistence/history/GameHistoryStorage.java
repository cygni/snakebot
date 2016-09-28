package se.cygni.snake.persistence.history;

import se.cygni.snake.eventapi.history.GameHistory;
import se.cygni.snake.eventapi.history.GameHistorySearchResult;

import java.util.Optional;

public interface GameHistoryStorage {

    void addGameHistory(GameHistory gameHistory);

    Optional<GameHistory> getGameHistory(String gameId);
    GameHistorySearchResult listGamesWithPlayer(String playerName);

}
