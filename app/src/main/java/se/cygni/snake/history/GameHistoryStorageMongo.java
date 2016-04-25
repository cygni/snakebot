package se.cygni.snake.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.cygni.snake.api.event.MapUpdateEvent;

import java.util.*;

@Component
@Profile({"production"})
public class GameHistoryStorageMongo implements GameHistoryStorage {

    private static Logger log = LoggerFactory
            .getLogger(GameHistoryStorageMongo.class);

    public GameHistoryStorageMongo() {
        log.debug("GameHistoryStorageMongo started");
    }

    @Override
    public void addToStorage(MapUpdateEvent mapUpdateEvent) {

    }

    @Override
    public List<MapUpdateEvent> getAllMapUpdatesForGame(String gameId) {
        List<MapUpdateEvent> mapUpdates = new ArrayList<>();
        return mapUpdates;
    }

    @Override
    public List<String> listGamesWithPlayer(String playerName) {
        List<String> games = new ArrayList<>();
        return games;
    }
}
