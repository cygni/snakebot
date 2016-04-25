package se.cygni.snake.history;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.event.InternalGameEvent;

@Service
public class GameHistory {

    private final EventBus eventBus;
    private final GameHistoryStorage storage;

    @Autowired
    public GameHistory(EventBus eventBus, GameHistoryStorage storage) {
        this.eventBus = eventBus;
        this.storage = storage;

        this.eventBus.register(this);
    }

    @Subscribe
    public void onInternalGameEvent(InternalGameEvent gameEvent) {

        if (!(gameEvent.getGameMessage() instanceof MapUpdateEvent)) {
            return;
        }

        storage.addToStorage((MapUpdateEvent)gameEvent.getGameMessage());
    }

}
