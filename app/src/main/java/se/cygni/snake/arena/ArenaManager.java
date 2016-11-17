package se.cygni.snake.arena;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.cygni.game.Player;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.api.request.RegisterPlayer;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.api.util.MessageUtils;
import se.cygni.snake.apiconversion.GameSettingsConverter;
import se.cygni.snake.game.Game;
import se.cygni.snake.game.GameFeatures;
import se.cygni.snake.game.GameManager;
import se.cygni.snake.player.RemotePlayer;
import se.cygni.snake.tournament.util.TournamentUtil;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

@Component
public class ArenaManager {
    private static final Logger log = LoggerFactory.getLogger(ArenaManager.class);
    private static final int ARENA_PLAYER_COUNT = 8;

    private final EventBus outgoingEventBus;
    private final EventBus incomingEventBus;

    private String arenaName;

    private GameManager gameManager;
    private Set<Player> connectedPlayers = new HashSet<>();
    private long secondsUntilNextGame = 0;
    private Game currentGame = null;

    @Autowired
    public ArenaManager(GameManager gameManager, EventBus globalEventBus) {
        this.gameManager = gameManager;

        this.outgoingEventBus = new EventBus("arena-outgoing");
        this.incomingEventBus = new EventBus("arens-incoming");

        incomingEventBus.register(this);
        globalEventBus.register(this);
    }

    @Subscribe
    public void registerPlayer(RegisterPlayer registerPlayer) {
        String playerId = registerPlayer.getReceivingPlayerId();

        Player player = new Player(registerPlayer.getPlayerName());
        player.setPlayerId(playerId);

        // TODO remove duplicated code and add password or similar anti-fakenicking
        if (connectedPlayers.contains(player)) {
            int removeDupWarning = 0;
            InvalidPlayerName playerNameTaken = new InvalidPlayerName(InvalidPlayerName.PlayerNameInvalidReason.Taken);
            MessageUtils.copyCommonAttributes(registerPlayer, playerNameTaken);
            playerNameTaken.setReceivingPlayerId(playerId);
            outgoingEventBus.post(playerNameTaken);
            return;
        }

        connectedPlayers.add(player);

        GameSettings gameSettings = GameSettingsConverter.toGameSettings(new GameFeatures());
        PlayerRegistered playerRegistered = new PlayerRegistered("not_yet_known", player.getName(), gameSettings, GameMode.ARENA);
        MessageUtils.copyCommonAttributes(registerPlayer, playerRegistered);

        outgoingEventBus.post(playerRegistered);

        log.debug("A player registered in the arena %s", arenaName);
    }

    @Subscribe
    public void registerMove(RegisterMove registerMove) {
        if (currentGame != null) {
            currentGame.registerMove(registerMove);
        }
    }

    public void playerLostConnection(String playerId) {
        Player player = new Player("name_unknown");
        player.setPlayerId(playerId);
        connectedPlayers.remove(player);

        if (currentGame != null) {
            currentGame.playerLostConnection(playerId);
        }
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 1000)
    public void runGameScheduler() {
        secondsUntilNextGame--;

        processCurrentGame();
        planNextGame();
    }

    private void processCurrentGame() {
        if (currentGame == null) {
            return;
        }

        if (currentGame.isEnded()) {
            processEndedGame();
            currentGame = null;
            // TODO set time left to 10 iff game is ended and time > 0 ???
        } else if (secondsUntilNextGame < 10) {
            log.warn("Arena game %s has exceeded maximum game time, aborting and starting a new game", currentGame.getGameId());
            currentGame.abort();
            currentGame = null;
        }
    }

    private void planNextGame() {
        if (connectedPlayers.size() < 2) {
            log.trace("Not enough players to start arena game");
            return;
        }

        if (secondsUntilNextGame <= 0) {
            // TODO This arbitrary formula (5 min between games) can use some tweaking
            secondsUntilNextGame = 60 * 5;
            startGame();
        } else {
            log.trace(String.format("Waiting %d seconds until next game", secondsUntilNextGame));
        }
    }

    private void startGame() {
        // TODO add a taboo list and prefer players that have not played before
        Set<Player> players = TournamentUtil.getRandomPlayers(connectedPlayers, ARENA_PLAYER_COUNT);

        currentGame = gameManager.createArenaGame();
        currentGame.setOutgoingEventBus(outgoingEventBus);
        players.forEach(player -> {
            // This object is mutable, we need a new one each game
            RemotePlayer remotePlayer = new RemotePlayer(player, outgoingEventBus);
            currentGame.addPlayer(remotePlayer);
        });
        currentGame.startGame();
        log.info("Started game in arena %s with id %s", arenaName, currentGame.getGameId());
    }

    private void processEndedGame() {
        // TODO calculate rankings
        // TODO store rankings
    }

    public EventBus getOutgoingEventBus() {
        return outgoingEventBus;
    }

    public EventBus getIncomingEventBus() {
        return incomingEventBus;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getArenaName() {
        return arenaName;
    }
}