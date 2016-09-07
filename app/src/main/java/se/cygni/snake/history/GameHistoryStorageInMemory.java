package se.cygni.snake.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.api.util.MessageUtils;
import se.cygni.snake.event.InternalGameEvent;

import java.util.*;
import java.util.stream.Collectors;

//@Profile({"development"})
@Component
public class GameHistoryStorageInMemory implements GameHistoryStorage {

    private static final int MAX_NOOF_GAMES_IN_MEMORY = 100;
    private static Logger log = LoggerFactory
            .getLogger(GameHistoryStorageInMemory.class);

    private final List<String> gameIds = new LinkedList<>();

    private Map<String, SortedSet<InternalGameEvent>> store = Collections.synchronizedMap(new HashMap<>());
    private Comparator<InternalGameEvent> internalGameEventComparator = (InternalGameEvent m1, InternalGameEvent m2) -> Long.compare(m1.getTstamp(), m2.getTstamp());

    public GameHistoryStorageInMemory() {
        log.debug("GameHistoryStorageInMemory started");
    }

    @Override
    public void addToStorage(InternalGameEvent internalGameEvent) {

        GameMessage gameMessage = internalGameEvent.getGameMessage();
        String gameId = MessageUtils.extractGameId(gameMessage);

        if (gameId == null) {
            log.debug("Received a GameEvent without gameId, discarding it. Type: {}", gameMessage.getType());
            return;
        }


        log.debug("Storing GameMessage for gameId: {}", gameId);

        if (!store.containsKey(gameId)) {
            gameIds.add(0, gameId);
            store.put(gameId, new TreeSet<>(internalGameEventComparator));
        }

        store.get(gameId).add(internalGameEvent);
    }


    @Override
    public List<GameMessage> getAllMessagesForGame(String gameId) {

        if (!store.containsKey(gameId)) {
            return new ArrayList<GameMessage>();
        }

        return store.get(gameId).stream().map(internalGameEvent -> {
            return internalGameEvent.getGameMessage();
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> listGamesWithPlayer(String playerName) {

        return gameIds.stream().filter(gameId -> {
            return hasGamePlayerWithName(gameId, playerName);
        }).collect(Collectors.toList());

    }

    private boolean hasGamePlayerWithName(String gameId, String name) {
        SortedSet<InternalGameEvent> events = store.get(gameId);

        if (events == null || events.size() == 0)
            return false;

        Optional<InternalGameEvent> firstMapUpdate =  events.stream().filter(internalGameEvent -> {
                return internalGameEvent.getGameMessage() instanceof MapUpdateEvent;
            }).findFirst();

        if (firstMapUpdate.isPresent()) {
            MapUpdateEvent mapUpdateEvent = (MapUpdateEvent)firstMapUpdate.get().getGameMessage();

            boolean containsName = Arrays.stream(mapUpdateEvent.getMap().getSnakeInfos()).map(SnakeInfo::getName).filter(snakename -> {
                return snakename.equals(name);
            }).findFirst().isPresent();

            return containsName;
        }

        return false;
    }

    @Scheduled(fixedDelay = 30000L)
    private void removeOldGames() {
        log.debug("Checking of stored game can be removed...");
        while (gameIds.size() > MAX_NOOF_GAMES_IN_MEMORY - 1) {
            String id = gameIds.get(gameIds.size() - 1);
            store.remove(id);
            gameIds.remove(id);
            log.debug("Removed gameId: {}", id);
        }
        log.debug("...done checking for removable games");
    }

}
