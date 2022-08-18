package se.cygni.snake.game;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.game.Player;
import se.cygni.game.enums.Direction;
import se.cygni.game.random.XORShiftRandom;
import se.cygni.snake.api.event.GameLinkEvent;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.request.ClientInfo;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.api.request.RegisterPlayer;
import se.cygni.snake.api.request.StartGame;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.api.util.MessageUtils;
import se.cygni.snake.apiconversion.DirectionConverter;
import se.cygni.snake.apiconversion.GameSettingsConverter;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.player.RemotePlayer;
import se.cygni.snake.player.bot.*;

import java.util.UUID;

public class Game {
    private static final Logger log = LoggerFactory.getLogger(Game.class);

    private final boolean trainingGame;
    private final EventBus incomingEventBus;
    private EventBus outgoingEventBus;
    private final String gameId;
    PlayerManager playerManager = new PlayerManager();
    private GameFeatures gameFeatures;
    private final GameEngine gameEngine;
    private final EventBus globalEventBus;
    private final String viewUrl;

    private XORShiftRandom botSelector = new XORShiftRandom();

    public Game(GameFeatures gameFeatures, EventBus globalEventBus, boolean trainingGame, String viewUrl) {

        this.globalEventBus = globalEventBus;
        this.gameFeatures = gameFeatures;
        this.trainingGame = trainingGame;
        this.viewUrl = viewUrl;
        gameId = UUID.randomUUID().toString();
        gameEngine = new GameEngine(gameFeatures, playerManager, gameId, globalEventBus);
        incomingEventBus = new EventBus("game-" + gameId + "-incoming");
        incomingEventBus.register(this);

        outgoingEventBus = new EventBus("game-" + gameId + "-outgoing");
    }

    public void setOutgoingEventBus(EventBus outgoingEventBus) {
        this.outgoingEventBus = outgoingEventBus;
    }

    @Subscribe
    public void startGame(StartGame startGame) {
        if (trainingGame) {
            log.info("Starting game: {}", gameId);
            startGame();
        }
    }

    @Subscribe
    public void registerPlayer(RegisterPlayer registerPlayer) {
        Player player = new Player(registerPlayer.getPlayerName());
        player.setPlayerId(registerPlayer.getReceivingPlayerId());

        if (playerManager.containsPlayerWithName(player.getName())) {
            InvalidPlayerName playerNameTaken = new InvalidPlayerName(InvalidPlayerName.PlayerNameInvalidReason.Taken);
            MessageUtils.copyCommonAttributes(registerPlayer, playerNameTaken);
            outgoingEventBus.post(playerNameTaken);
            return;
        }

        RemotePlayer remotePlayer = new RemotePlayer(player, outgoingEventBus);
        addPlayer(remotePlayer);

        // If this is a training game changes to settings are allowed
        GameSettings requestedGameSettings = registerPlayer.getGameSettings();
        if (trainingGame && requestedGameSettings != null) {
            gameFeatures = GameSettingsConverter.toGameFeatures(requestedGameSettings, gameFeatures);
            gameEngine.reApplyGameFeatures(gameFeatures);
        }

        GameSettings gameSettings = GameSettingsConverter.toGameSettings(gameFeatures);
        PlayerRegistered playerRegistered = new PlayerRegistered(gameId, player.getName(), gameSettings, GameMode.TRAINING);
        MessageUtils.copyCommonAttributes(registerPlayer, playerRegistered);

        outgoingEventBus.post(playerRegistered);
        sendGameLink(player.getPlayerId());
        publishGameChanged();
    }

    @Subscribe
    public void registerMove(RegisterMove registerMove) {
        long gameTick = registerMove.getGameTick();
        String playerId = registerMove.getReceivingPlayerId();
        Direction direction = DirectionConverter.toDirection(registerMove.getDirection());

        if (!gameId.equals(registerMove.getGameId())) {
            log.warn("Player: {}, playerId: {}, tried to register move for wrong game. Aborting that move.",
                    playerManager.getPlayerName(playerId),
                    playerId);
            return;
        }

        gameEngine.registerMove(
                gameTick,
                playerId,
                direction
        );
    }

    @Subscribe
    public void clientInfo(ClientInfo clientInfo) {
        log.info("Client Info: {}", clientInfo);
        globalEventBus.post(clientInfo);
    }

    public void startGame() {
        if (gameEngine.isGameRunning()) {
            return;
        }

        if (!trainingGame) { // Game link was already sent in the RegisterPlayer event
            playerManager.getLiveAndRemotePlayers().forEach(player -> {
                sendGameLink(player.getPlayerId());
            });
        }

        initBotPlayers();
        gameEngine.startGame();
    }

    public void addPlayer(IPlayer player) {
        playerManager.add(player);
        publishGameChanged();
    }

    private void sendGameLink(String playerId) {
        GameLinkEvent gle = new GameLinkEvent(gameId, viewUrl + gameId);
        gle.setReceivingPlayerId(playerId);
        outgoingEventBus.post(gle);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
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

    public void playerLostConnection(String playerId) {
        try {
            IPlayer player = playerManager.getPlayer(playerId);
            player.dead(gameEngine.getCurrentWorldTick());
            log.info("Player: {} , playerId: {} lost connection and was therefore killed.", player.getName(), playerId);
        } catch (Exception e) {
            log.warn("PlayerId: {} lost connection but I could not remove her (which is OK, she probably wasn't registered in the first place)", playerId);
        }
        if (playerManager.getLiveAndRemotePlayers().size() == 0) {
            abort();
        } else {
            publishGameChanged();
        }
    }

    public EventBus getGlobalEventBus() {
        return globalEventBus;
    }

    public boolean isEnded() {
        return gameEngine.isGameComplete();
    }

    public GameResult getGameResult() {
        return gameEngine.getGameResult();
    }

    private void initBotPlayers() {
        if (!trainingGame)
            return;

        for (int i = 0; i < gameFeatures.getMaxNoofPlayers() - 1; i++) {
            BotPlayer bot;
            bot = new StraightBot(UUID.randomUUID().toString(), incomingEventBus);

            // switch (Math.abs(botSelector.nextInt() % 5)) {
            //     case 0:
            //         bot = new RandomBot(UUID.randomUUID().toString(), incomingEventBus);
            //         break;
            //     case 1:
            //         bot = new StayAliveBot(UUID.randomUUID().toString(), incomingEventBus);
            //         break;
            //     case 2:
            //         bot = new StraightBot(UUID.randomUUID().toString(), incomingEventBus);
            //         break;
            //     case 3:
            //         bot = new BrainySnakePlayer(UUID.randomUUID().toString(), incomingEventBus);
            //         break;
            //     case 4:
            //         bot = new Snakey(UUID.randomUUID().toString(), incomingEventBus);
            //         break;
            //     default:
            //         bot = new StraightBot(UUID.randomUUID().toString(), incomingEventBus);
            //         break;
            // }

            addPlayer(bot);
        }
    }

    public void abort() {
        playerManager.clear();
        gameEngine.abort();

        InternalGameEvent gevent = new InternalGameEvent(System.currentTimeMillis());
        gevent.onGameAborted(getGameId());
        globalEventBus.post(gevent);
        globalEventBus.post(gevent.getGameMessage());
    }

    public void publishGameChanged() {
        InternalGameEvent gevent = new InternalGameEvent(System.currentTimeMillis());
        gevent.onGameChanged(getGameId());
        globalEventBus.post(gevent);
    }
}
