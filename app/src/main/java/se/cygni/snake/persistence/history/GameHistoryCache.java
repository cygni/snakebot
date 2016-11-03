package se.cygni.snake.persistence.history;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.event.*;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.api.util.MessageUtils;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.eventapi.history.GameHistory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GameHistoryCache {

    private static Logger log = LoggerFactory
            .getLogger(GameHistoryCache.class);

    private static int MAX_AGE_TILL_AUTO_PERSIST_IN_MS = 5 * 60 * 1000;

    private final List<String> gameIds = Collections.synchronizedList(new LinkedList<>());
    private final Map<String, SortedSet<InternalGameEvent>> store = Collections.synchronizedMap(new HashMap<>());
    private final Comparator<InternalGameEvent> internalGameEventComparator = (InternalGameEvent m1, InternalGameEvent m2) -> Long.compare(m1.getTstamp(), m2.getTstamp());

    private final EventBus eventBus;

    @Autowired
    public GameHistoryCache(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Subscribe
    public void onGameEvent(InternalGameEvent internalGameEvent) {
        GameMessage gameMessage = internalGameEvent.getGameMessage();
        Optional<String> extractGameId = MessageUtils.extractGameId(gameMessage);

        if (!extractGameId.isPresent()) {
            log.debug("Received a GameEvent without gameId, discarding it. Type: {}", gameMessage.getClass());
            return;
        }

        String gameId = extractGameId.get();

        if (storableMessages.contains(gameMessage.getClass())) {
            if (!store.containsKey(gameId)) {
                gameIds.add(0, gameId);
                store.put(gameId, new TreeSet<>(internalGameEventComparator));
            }

            log.debug("Storing GameMessage: {} for gameId: {}", gameMessage.getClass(), gameId);
            store.get(gameId).add(internalGameEvent);
        }

        if (gameEndedMessages.contains(gameMessage.getClass())) {
            log.debug("Going to persist gameId: {} on cause of: {}", gameId, gameMessage.getClass());
            persistAndRemoveGame(gameId);
        }
    }

    public List<GameHistory> getFinishedGames() {

        return new ArrayList<>();
    }

    private void persistAndRemoveGame(String gameId) {
        GameHistory gameHistory = getGameHistory(gameId);
        if (gameHistory != null) {
            log.debug("Persisting gameId: {}", gameId);
            eventBus.post(gameHistory);
            gameIds.remove(gameId);
            store.remove(gameId);
        }
    }

    private GameHistory getGameHistory(String gameId) {
        if (!store.containsKey(gameId)) {
            return null;
        }

        return new GameHistory(
                gameId,
                getPlayersForGame(gameId),
                extractGameDate(gameId),
                getGameMessagesForGame(gameId));
    }

    private List<GameMessage> getGameMessagesForGame(String gameId) {
        if (!store.containsKey(gameId)) {
            return new ArrayList<GameMessage>();
        }

        return store.get(gameId).stream().map(internalGameEvent -> {
            return internalGameEvent.getGameMessage();
        }).collect(Collectors.toList());
    }

    private LocalDateTime extractGameDate(String gameId) {
        Optional<InternalGameEvent> firstMapUpdate = getFirstInternalGameEvent(gameId);

        if(firstMapUpdate.isPresent()) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(firstMapUpdate.get().getTstamp()), TimeZone.getDefault().toZoneId());
        }

        return null;
    }

    private String[] getPlayersForGame(String gameId) {
        SortedSet<InternalGameEvent> events = store.get(gameId);

        if (events == null || events.size() == 0)
            return new String[0];

        Optional<InternalGameEvent> firstMapUpdate =  getFirstInternalGameEvent(gameId);

        if (firstMapUpdate.isPresent()) {
            MapUpdateEvent mapUpdateEvent = (MapUpdateEvent)firstMapUpdate.get().getGameMessage();

            return Arrays.stream(mapUpdateEvent.getMap().getSnakeInfos())
                    .map(SnakeInfo::getName)
                    .collect(Collectors.toList()).toArray(new String[0]);
        }

        return new String[0];
    }

    private Optional<InternalGameEvent> getFirstInternalGameEvent(String gameId) {
        SortedSet<InternalGameEvent> events = store.get(gameId);

        if (events == null || events.size() == 0)
            return Optional.ofNullable(null);

        return events.stream().filter(internalGameEvent -> {
            return internalGameEvent.getGameMessage() instanceof MapUpdateEvent;
        }).findFirst();
    }

    private final Set<Class<? extends GameMessage>> storableMessages = new HashSet<Class<? extends GameMessage>>(){{
        add(GameCreatedEvent.class);
        add(MapUpdateEvent.class);
        add(GameLinkEvent.class);
        add(GameStartingEvent.class);
        add(SnakeDeadEvent.class);
        add(GameEndedEvent.class);
        add(TournamentEndedEvent.class);
        add(GameResultEvent.class);
    }};

    private final Set<Class<? extends GameMessage>> gameEndedMessages = new HashSet<Class<? extends GameMessage>>(){{
        add(GameEndedEvent.class);
        add(GameAbortedEvent.class);
        add(TournamentEndedEvent.class);
    }};
}
