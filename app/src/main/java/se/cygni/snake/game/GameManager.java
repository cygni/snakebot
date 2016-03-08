package se.cygni.snake.game;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.cygni.snake.api.event.GameEndedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameManager {

    private static Logger log = LoggerFactory
            .getLogger(GameManager.class);

    EventBus globalEventBus;

    private Map<String, Game> activeGames = new ConcurrentHashMap<>(new HashMap<>());

    @Autowired
    public GameManager(EventBus globalEventBus) {
        this.globalEventBus = globalEventBus;
        globalEventBus.register(this);
    }

    public Game createTrainingGame() {
        GameFeatures gameFeatures = new GameFeatures();
        gameFeatures.trainingGame = true;
        gameFeatures.height = 25;
        gameFeatures.width = 25;
        Game game = new Game(gameFeatures, globalEventBus);

        registerGame(game);
        return game;
    }

    public Game createGame(GameFeatures gameFeatures) {
        Game game = new Game(gameFeatures, globalEventBus);
        registerGame(game);
        return game;
    }

    public String[] listGameIds() {
        activeGames.values().stream().forEach(game -> {
            log.info("gameId: {}, active remote players: {}", game.getGameId(), game.getLiveAndRemotePlayers().size());
        });
        return activeGames
                .keySet()
                .stream()
                .filter(id -> {
                    return getGame(id).getLiveAndRemotePlayers().size() > 0;
                })
                .toArray(size -> new String[size]);
    }

    public Game getGame(String gameId) {
        return activeGames.get(gameId);
    }

    private void registerGame(Game game) {
        activeGames.put(game.getGameId(), game);
    }

    @Subscribe
    public void onGameEndedEvent(GameEndedEvent gameEndedEvent) {
        activeGames.remove(gameEndedEvent.getGameId());
    }
}
