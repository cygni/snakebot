package se.cygni.snake.game;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.cygni.snake.api.event.GameAbortedEvent;
import se.cygni.snake.api.event.GameCreatedEvent;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.event.InternalGameEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class GameManager {

    private static final Logger log = LoggerFactory.getLogger(GameManager.class);

    EventBus globalEventBus;

    private Map<String, Game> activeGames = new ConcurrentHashMap<>(new HashMap<>());

    @Autowired
    public GameManager(EventBus globalEventBus) {
        this.globalEventBus = globalEventBus;
        globalEventBus.register(this);
    }

    public Game createTrainingGame() {
        GameFeatures gameFeatures = new GameFeatures();
        gameFeatures.setTrainingGame(true);
        gameFeatures.setHeight(25);
        gameFeatures.setWidth(25);
        Game game = new Game(gameFeatures, globalEventBus, true);

        registerGame(game);
        return game;
    }

    public Game createGame(GameFeatures gameFeatures) {
        Game game = new Game(gameFeatures, globalEventBus, false);
        registerGame(game);

        return game;
    }

    public List<Game> listAllGames() {
        return activeGames
                .keySet()
                .stream()
                .map(id -> {
                    return getGame(id);
                })
                .collect(Collectors.toList());
    }

    public List<Game> listActiveGames() {
        return activeGames
                .keySet()
                .stream()
                .filter(id -> {
                    return getGame(id).getPlayerManager().getLiveAndRemotePlayers().size() > 0;
                }).map(id -> {
                    return getGame(id);
                }).collect(Collectors.toList());
    }

    public String[] listGameIds() {

        return activeGames
                .keySet()
                .stream()
                .filter(id -> {
                    return getGame(id).getPlayerManager().getLiveAndRemotePlayers().size() > 0;
                })
                .toArray(size -> new String[size]);
    }

    public Game getGame(String gameId) {
        return activeGames.get(gameId);
    }

    private void registerGame(Game game) {
        activeGames.put(game.getGameId(), game);

        log.info("Registered new game, posting to GlobalEventBus...");
        globalEventBus.post(new InternalGameEvent(
                System.currentTimeMillis(),
                new GameCreatedEvent(game.getGameId())));
    }

    @Subscribe
    public void onGameEndedEvent(GameEndedEvent gameEndedEvent) {
        activeGames.remove(gameEndedEvent.getGameId());
    }

    @Subscribe
    public void onGameAbortedEvent(GameAbortedEvent gameAbortedEvent) {
        activeGames.remove(gameAbortedEvent.getGameId());
    }
}
