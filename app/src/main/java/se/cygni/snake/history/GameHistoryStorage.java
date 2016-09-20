package se.cygni.snake.history;

import se.cygni.snake.history.repository.GameHistory;
import se.cygni.snake.history.repository.GameHistorySearchResult;

import java.util.Optional;

public interface GameHistoryStorage {

    void addGameHistory(GameHistory gameHistory);

    Optional<GameHistory> getGameHistory(String gameId);
    GameHistorySearchResult listGamesWithPlayer(String playerName);

}
