package se.cygni.snake.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.cygni.snake.api.event.MapUpdateEvent;

import java.util.*;

@Component
@Profile({"development"})
public class GameHistoryStorageInMemory implements GameHistoryStorage {

    private static Logger log = LoggerFactory
            .getLogger(GameHistoryStorageInMemory.class);

    private static final int MAX_NOOF_GAMES_IN_MEMORY = 10;
    private final List<String> gameIds = new LinkedList<>();

    private Map<String, SortedSet<MapUpdateEvent>> store = Collections.synchronizedMap(new HashMap<>());
    private Comparator<MapUpdateEvent> byWorldTick = (MapUpdateEvent m1, MapUpdateEvent m2) -> Long.compare(m1.getGameTick(), m2.getGameTick());

    public GameHistoryStorageInMemory() {
        log.debug("GameHistoryStorageInMemory started");
    }

    @Override
    public void addToStorage(MapUpdateEvent mapUpdateEvent) {
        log.debug("Storing map update for gameId: {}, worldTick: {}", mapUpdateEvent.getGameId(), mapUpdateEvent.getGameTick());

        String gameId = mapUpdateEvent.getGameId();
        if (!store.containsKey(gameId)) {
            if (gameIds.size() >= MAX_NOOF_GAMES_IN_MEMORY) {
                removeOldGames();
            }
            gameIds.add(0, gameId);
            store.put(gameId, new TreeSet<>(byWorldTick));
        }

        store.get(gameId).add(mapUpdateEvent);
    }

    private void removeOldGames() {
        while (gameIds.size() > MAX_NOOF_GAMES_IN_MEMORY - 1) {
            String id = gameIds.get(gameIds.size()-1);
            store.remove(id);
            gameIds.remove(id);
        }
    }

    @Override
    public List<MapUpdateEvent> getAllMapUpdatesForGame(String gameId) {
        List<MapUpdateEvent> mapUpdates = new ArrayList<>();

        if (!store.containsKey(gameId)) {
            return mapUpdates;
        }

        mapUpdates.addAll(store.get(gameId));
        return mapUpdates;
    }

    @Override
    public List<String> listGamesWithPlayer(String playerName) {
        return null;
    }
}
