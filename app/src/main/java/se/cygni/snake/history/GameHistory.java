package se.cygni.snake.history;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.cygni.snake.event.InternalGameEvent;

@Service
public class GameHistory {

    private static Logger log = LoggerFactory
            .getLogger(GameHistory.class);

    private final EventBus eventBus;
    private final GameHistoryStorage storage;

    @Autowired
    public GameHistory(EventBus eventBus, GameHistoryStorage storage) {
        this.eventBus = eventBus;
        this.storage = storage;

        this.eventBus.register(this);

        log.debug("Created GameHistory collector and registered to eventbus: {}", eventBus.toString());
    }

    @Subscribe
    public void onInternalGameEvent(InternalGameEvent gameEvent) {

        log.debug("Got an InternalGameEvent");

        storage.addToStorage(gameEvent);

    }

}
