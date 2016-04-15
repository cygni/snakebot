package se.cygni.snake.tournament;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.cygni.game.Player;
import se.cygni.game.enums.Direction;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.exception.NoActiveTournament;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.api.request.RegisterPlayer;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.api.util.MessageUtils;
import se.cygni.snake.apiconversion.DirectionConverter;
import se.cygni.snake.apiconversion.GameSettingsConverter;
import se.cygni.snake.game.Game;
import se.cygni.snake.game.GameFeatures;
import se.cygni.snake.game.GameManager;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.player.RemotePlayer;

import java.util.*;

@Component
public class TournamentManager {

    private GameManager gameManager;
    private final EventBus outgoingEventBus;
    private final EventBus incomingEventBus;
    private final EventBus globalEventBus;

    private boolean tournamentActive;
    private boolean tournamentStarted;
    private String tournamentId;
    private String tournamentName;
    private GameFeatures gameFeatures;
    private TournamentPlan tournamentPlan;
    private Set<IPlayer> players = Collections.synchronizedSet(new HashSet<>());

    private Map<String, Game> games = new HashMap<>();

    @Autowired
    public TournamentManager(GameManager gameManager, EventBus globalEventBus) {
        this.gameManager = gameManager;
        this.globalEventBus = globalEventBus;

        this.outgoingEventBus = new EventBus("tournament-outgoing");
        this.incomingEventBus = new EventBus("tournament-incoming");

        incomingEventBus.register(this);
    }

    public void killTournament() {
        tournamentActive = false;
        tournamentStarted = false;

        tournamentId = null;
        tournamentName = null;
        gameFeatures = null;
        tournamentPlan = null;

        games.values().forEach(game -> game.abort());
    }

    public void createTournament(String name) {
        if (isTournamentActive() || isTournamentStarted())
            throw new RuntimeException("A tournament is already active");

        killTournament();

        tournamentActive = true;
        tournamentId = UUID.randomUUID().toString();
        tournamentName = name;
        gameFeatures = new GameFeatures();
        games = new HashMap<>();
    }



    public void startGame(String gameId) {

    }

    public void planTournament() {
        tournamentPlan = new TournamentPlan(gameFeatures, players);
    }

    @Subscribe
    public void registerPlayer(RegisterPlayer registerPlayer) {

        String playerId = registerPlayer.getReceivingPlayerId();

        if (!isTournamentActive() || isTournamentStarted()) {
            NoActiveTournament notActive = new NoActiveTournament();
            notActive.setReceivingPlayerId(playerId);
            outgoingEventBus.post(notActive);
            return;
        }

        Player player = new Player(registerPlayer.getPlayerName());
        player.setPlayerId(playerId);

        if (players.contains(player)) {
            InvalidPlayerName playerNameTaken = new InvalidPlayerName(InvalidPlayerName.PlayerNameInvalidReason.Taken);
            MessageUtils.copyCommonAttributes(registerPlayer, playerNameTaken);
            playerNameTaken.setReceivingPlayerId(playerId);
            outgoingEventBus.post(playerNameTaken);
            return;
        }

        RemotePlayer remotePlayer = new RemotePlayer(player, outgoingEventBus);
        addPlayer(remotePlayer);

        GameSettings gameSettings = GameSettingsConverter.toGameSettings(gameFeatures);
        PlayerRegistered playerRegistered = new PlayerRegistered("not_yet_known", player.getName(), gameSettings, GameMode.TOURNAMENT);
        MessageUtils.copyCommonAttributes(registerPlayer, playerRegistered);

        outgoingEventBus.post(playerRegistered);
    }

    @Subscribe
    public void registerMove(RegisterMove registerMove) {
        long gameTick = registerMove.getGameTick();
        String playerId = registerMove.getReceivingPlayerId();
        Direction direction = DirectionConverter.toDirection(registerMove.getDirection());

        // Send move to active game
    }

    public void addPlayer(IPlayer player) {
        players.add(player);
    }

    public EventBus getOutgoingEventBus() {
        return outgoingEventBus;
    }

    public EventBus getIncomingEventBus() {
        return incomingEventBus;
    }

    public boolean isTournamentActive() {
        return tournamentActive;
    }

    public boolean isTournamentStarted() {
        return tournamentStarted;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setGameFeatures(GameFeatures gameFeatures) {
        this.gameFeatures = gameFeatures;
    }

    public GameFeatures getGameFeatures() {
        return gameFeatures;
    }
}
