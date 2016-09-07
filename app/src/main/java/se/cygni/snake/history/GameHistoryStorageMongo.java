package se.cygni.snake.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.event.InternalGameEvent;

import java.util.List;

//@Component
//@Profile({"production"})
public class GameHistoryStorageMongo implements GameHistoryStorage {

    private static Logger log = LoggerFactory
            .getLogger(GameHistoryStorageMongo.class);

    public GameHistoryStorageMongo() {
        log.debug("GameHistoryStorageMongo started");
    }

    @Override
    public void addToStorage(InternalGameEvent internalGameEvent) {

    }

    @Override
    public List<GameMessage> getAllMessagesForGame(String gameId) {
        return null;
    }

    @Override
    public List<String> listGamesWithPlayer(String playerName) {
        return null;
    }
}
