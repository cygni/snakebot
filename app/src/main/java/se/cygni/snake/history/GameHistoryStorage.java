package se.cygni.snake.history;

import se.cygni.snake.api.GameMessage;
import se.cygni.snake.event.InternalGameEvent;

import java.util.List;

public interface GameHistoryStorage {

    void addToStorage(InternalGameEvent internalGameEvent);

    List<GameMessage> getAllMessagesForGame(String gameId);
    List<String> listGamesWithPlayer(String playerName);

}
