package se.cygni.snake.history;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.history.repository.GameHistory;
import se.cygni.snake.history.repository.GameHistorySearchResult;

import java.util.Optional;

//@Component
//@Profile({"production"})
public class GameHistoryStorageMongo implements GameHistoryStorage {

    private static Logger log = LoggerFactory
            .getLogger(GameHistoryStorageMongo.class);

    private final EventBus eventBus;

    public GameHistoryStorageMongo(EventBus eventBus) {
        log.debug("GameHistoryStorageMongo started");

        this.eventBus = eventBus;
        this.eventBus.register(this);
    }


    @Override
    @Subscribe
    public void addGameHistory(GameHistory gameHistory) {

    }

    @Override
    public Optional<GameHistory> getGameHistory(String gameId) {
        return null;
    }

    @Override
    public GameHistorySearchResult listGamesWithPlayer(String playerName) {
        return null;
    }
}
