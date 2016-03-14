package se.cygni.snake.game;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.game.Player;
import se.cygni.game.enums.Direction;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.api.request.RegisterPlayer;
import se.cygni.snake.api.request.StartGame;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.api.util.MessageUtils;
import se.cygni.snake.apiconversion.DirectionConverter;
import se.cygni.snake.apiconversion.GameSettingsConverter;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.player.RemotePlayer;
import se.cygni.snake.player.bot.RandomBot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Game {
    private static Logger log = LoggerFactory
            .getLogger(Game.class);

    private final EventBus incomingEventBus;
    private final EventBus outgoingEventBus;
    private final String gameId;
    private Set<IPlayer> players = Collections.synchronizedSet(new HashSet<>());
    private GameFeatures gameFeatures;
    private final GameEngine gameEngine;
    private final EventBus globalEventBus;

    public Game(GameFeatures gameFeatures, EventBus globalEventBus) {

        this.globalEventBus = globalEventBus;
        this.gameFeatures = gameFeatures;
        gameEngine = new GameEngine(gameFeatures, this, globalEventBus);
        gameId = UUID.randomUUID().toString();
        incomingEventBus = new EventBus("game-" + gameId + "-incoming");
        incomingEventBus.register(this);

        outgoingEventBus = new EventBus("game-" + gameId + "-outgoing");
    }

    @Subscribe
    public void startGame(StartGame startGame) {
        if (gameFeatures.trainingGame) {
            log.debug("Starting game");
            startGame();
        }
    }

    @Subscribe
    public void registerPlayer(RegisterPlayer registerPlayer) {
        Player player = new Player(registerPlayer.getPlayerName(), registerPlayer.getColor());
        player.setPlayerId(registerPlayer.getReceivingPlayerId());

        if (players.contains(player)) {
            InvalidPlayerName playerNameTaken = new InvalidPlayerName(InvalidPlayerName.PlayerNameInvalidReason.Taken);
            MessageUtils.copyCommonAttributes(registerPlayer, playerNameTaken);
            outgoingEventBus.post(playerNameTaken);
            return;
        }

        RemotePlayer remotePlayer = new RemotePlayer(player, outgoingEventBus);
        addPlayer(remotePlayer);

        // If this is a training game changes to settings are allowed
        GameSettings requestedGameSettings = registerPlayer.getGameSettings();
        if (gameFeatures.trainingGame && requestedGameSettings != null) {
            gameFeatures = GameSettingsConverter.toGameFeatures(requestedGameSettings);
            gameFeatures.trainingGame = true; // Just to be sure
            gameEngine.reApplyGameFeatures(gameFeatures);
        }

        GameSettings gameSettings = GameSettingsConverter.toGameSettings(gameFeatures);
        PlayerRegistered playerRegistered = new PlayerRegistered(gameId, player.getName(), player.getColor(), gameSettings, GameMode.training);
        MessageUtils.copyCommonAttributes(registerPlayer, playerRegistered);

        outgoingEventBus.post(playerRegistered);
    }

    @Subscribe
    public void registerMove(RegisterMove registerMove) {
        long gameTick = registerMove.getGameTick();
        String playerId = registerMove.getReceivingPlayerId();
        Direction direction = DirectionConverter.toDirection(registerMove.getDirection());
        gameEngine.registerMove(
                gameTick,
                playerId,
                direction
        );
    }

    public void startGame() {
        if (gameEngine.isGameRunning()) {
            return;
        }

        initBotPlayers();
        gameEngine.startGame();
    }

    public void addPlayer(IPlayer player) {
        players.add(player);
    }

    public Set<IPlayer> getPlayers() {
        return players;
    }

    public int getNoofPlayers() {
        return players.size();
    }

    public IPlayer getPlayer(String playerId) {
        return players.stream().filter(player -> player.getPlayerId().equals(playerId)).findFirst().get();
    }

    public EventBus getOutgoingEventBus() {
        return outgoingEventBus;
    }

    public EventBus getIncomingEventBus() {
        return incomingEventBus;
    }

    public String getGameId() {
        return gameId;
    }

    public GameFeatures getGameFeatures() {
        return gameFeatures;
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public Set<IPlayer> getLivePlayers() {
        return getPlayers().stream().filter(player ->
            player.isAlive()
        ).collect(Collectors.toSet());
    }

    public Set<IPlayer> getLiveAndRemotePlayers() {
        return getPlayers().stream().filter(player ->
                player.isAlive() && player instanceof RemotePlayer
        ).collect(Collectors.toSet());
    }

    public void playerLostConnection(String playerId) {
        getPlayer(playerId).dead();
    }

    public EventBus getGlobalEventBus() {
        return globalEventBus;
    }

    private void initBotPlayers() {
        if (!gameFeatures.trainingGame)
            return;

        for (int i = 0; i < gameFeatures.maxNoofPlayers - 1; i++) {
            RandomBot rbot = new RandomBot(UUID.randomUUID().toString(), incomingEventBus);
            addPlayer(rbot);
        }
    }

    public void abort() {
        players.clear();
        gameEngine.abort();
    }
}
