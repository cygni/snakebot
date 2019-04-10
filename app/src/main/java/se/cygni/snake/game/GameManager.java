package se.cygni.snake.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import se.cygni.snake.api.event.GameAbortedEvent;
import se.cygni.snake.api.event.GameCreatedEvent;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.event.InternalGameEvent;

public class GameManager {

    private static final Logger log = LoggerFactory.getLogger(GameManager.class);

    EventBus globalEventBus;

    private final Map<String, Game> activeGames = new ConcurrentHashMap<>(new HashMap<>());

    @Value("${snakebot.view.url}")
    private String viewUrl;

    @Autowired
    public GameManager(EventBus globalEventBus) {
	this.globalEventBus = globalEventBus;
	globalEventBus.register(this);
    }

    public Game createArenaGame() {
	final GameFeatures gameFeatures = new GameFeatures();
	final Game game = new Game(gameFeatures, globalEventBus, false, viewUrl);
	registerGame(game);

	return game;
    }

    public Game createGame(GameFeatures gameFeatures) {
	final Game game = new Game(gameFeatures, globalEventBus, false, viewUrl);
	registerGame(game);

	return game;
    }

    public Game createTrainingGame() {
	final GameFeatures gameFeatures = new GameFeatures();
	gameFeatures.setTrainingGame(true);
	final Game game = new Game(gameFeatures, globalEventBus, true, viewUrl);

	registerGame(game);
	return game;
    }

    public Game getGame(String gameId) {
	return activeGames.get(gameId);
    }

    public List<Game> listActiveGames() {
	return activeGames.keySet().stream().filter(id -> {
	    return getGame(id).getPlayerManager().getLiveAndRemotePlayers().size() > 0;
	}).map(id -> {
	    return getGame(id);
	}).collect(Collectors.toList());
    }

    public List<Game> listAllGames() {
	return activeGames.keySet().stream().map(id -> {
	    return getGame(id);
	}).collect(Collectors.toList());
    }

    public String[] listGameIds() {

	return activeGames.keySet().stream().filter(id -> {
	    return getGame(id).getPlayerManager().getLiveAndRemotePlayers().size() > 0;
	}).toArray(size -> new String[size]);
    }

    @Subscribe
    public void onGameAbortedEvent(GameAbortedEvent gameAbortedEvent) {
	activeGames.remove(gameAbortedEvent.getGameId());
    }

    @Subscribe
    public void onGameEndedEvent(GameEndedEvent gameEndedEvent) {
	activeGames.remove(gameEndedEvent.getGameId());
    }

    private void registerGame(Game game) {
	activeGames.put(game.getGameId(), game);

	log.info("Registered new game, posting to GlobalEventBus...");
	globalEventBus.post(new InternalGameEvent(System.currentTimeMillis(), new GameCreatedEvent(game.getGameId())));
    }
}
