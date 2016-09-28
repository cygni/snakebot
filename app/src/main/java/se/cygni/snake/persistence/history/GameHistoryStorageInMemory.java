package se.cygni.snake.persistence.history;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.cygni.snake.eventapi.history.GameHistory;
import se.cygni.snake.eventapi.history.GameHistorySearchItem;
import se.cygni.snake.eventapi.history.GameHistorySearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Profile({"!production"})
@Component
public class GameHistoryStorageInMemory implements GameHistoryStorage {

    private static final int MAX_NOOF_GAMES_IN_MEMORY = 100;
    private static Logger log = LoggerFactory
            .getLogger(GameHistoryStorageInMemory.class);

    private final EventBus eventBus;

    private List<GameHistory> gameHistories = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    public GameHistoryStorageInMemory(EventBus eventBus) {
        log.debug("GameHistoryStorageInMemory started");

        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Override
    @Subscribe
    public void addGameHistory(GameHistory gameHistory) {
        log.debug("Adding GameHistory to memory!");
        gameHistories.add(gameHistory);
    }

    @Override
    public Optional<GameHistory> getGameHistory(String gameId) {
        return gameHistories
                .stream()
                .filter(gameHistory -> gameHistory.getGameId().equals(gameId))
                .findFirst();
    }

    @Override
    public GameHistorySearchResult listGamesWithPlayer(String playerName) {
        GameHistorySearchResult result = new GameHistorySearchResult();

        List<GameHistorySearchItem> items = gameHistories
                .stream()
                .filter(gameHistory -> ArrayUtils.contains(gameHistory.getPlayerNames(), playerName))
                .map(gameHistory -> {
                    return new GameHistorySearchItem(
                            gameHistory.getGameId(),
                            gameHistory.getPlayerNames(),
                            gameHistory.getGameDate()
                    );
                })
                .collect(Collectors.toList());

        result.setItems(items);
        return result;
    }

    @Scheduled(fixedDelay = 30000L)
    private void removeOldGames() {
        while (gameHistories.size() > MAX_NOOF_GAMES_IN_MEMORY - 1) {
            GameHistory gameHistory = gameHistories.remove(0);
            log.debug("Removed gameId: {}", gameHistory.getGameId());
        }
    }

}
