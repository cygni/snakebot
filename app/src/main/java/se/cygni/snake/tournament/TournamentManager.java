package se.cygni.snake.tournament;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.cygni.game.Player;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.TournamentEndedEvent;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.exception.NoActiveTournament;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.model.PlayerPoints;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.api.request.RegisterPlayer;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.api.util.MessageUtils;
import se.cygni.snake.apiconversion.GameSettingsConverter;
import se.cygni.snake.apiconversion.TournamentPlanConverter;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.eventapi.model.TournamentGamePlan;
import se.cygni.snake.game.*;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.player.RemotePlayer;
import se.cygni.snake.tournament.util.TournamentUtil;

import java.util.*;

@Component
public class TournamentManager {
    private static final Logger log = LoggerFactory.getLogger(TournamentManager.class);

    private GameManager gameManager;
    private final EventBus outgoingEventBus;
    private final EventBus incomingEventBus;
    private final EventBus globalEventBus;

    private boolean tournamentActive = false;
    private boolean tournamentStarted = false;
    private String tournamentId;
    private String tournamentName;
    private GameFeatures gameFeatures = new GameFeatures();
    private TournamentPlan tournamentPlan;
    private PlayerManager playerManager = new PlayerManager();
    private PlayerManager playersStillInTournament = new PlayerManager();
    private int currentLevel = 0;
    private HashMap<String, Game> games = new HashMap<>();

    @Autowired
    public TournamentManager(GameManager gameManager, EventBus globalEventBus) {
        this.gameManager = gameManager;
        this.globalEventBus = globalEventBus;

        this.outgoingEventBus = new EventBus("tournament-outgoing");
        this.incomingEventBus = new EventBus("tournament-incoming");

        incomingEventBus.register(this);
        globalEventBus.register(this);
    }

    public void killTournament() {
        tournamentActive = false;
        tournamentStarted = false;

        tournamentId = null;
        tournamentName = null;
        gameFeatures = null;
        tournamentPlan = null;
        currentLevel = 0;

        playerManager.clear();
        playersStillInTournament.clear();
        games.values().forEach(game -> game.abort());
        games.clear();
    }

    public void createTournament(String name) {
        if (isTournamentActive() || isTournamentStarted())
            throw new RuntimeException("A tournament is already active");

        killTournament();

        tournamentActive = true;
        tournamentId = UUID.randomUUID().toString();
        tournamentName = name;
        gameFeatures = new GameFeatures();
    }

    private boolean isTournamentComplete() {
        return currentLevel >= tournamentPlan.getLevels().size();
    }

    private void completeTournament() {
        log.info("We have a tournament result!");

        TournamentPlannedGame lastGame = tournamentPlan.getLevelAt(currentLevel-1).getPlannedGames().get(0);
        GameResult gameResult = lastGame.getGame().getGameResult();

        List<PlayerPoints> playerPoints = new ArrayList<>();
        String winnerPlayerId = null;
        if (gameResult.getWinner() != null) {
            winnerPlayerId = gameResult.getWinner().getPlayerId();
            int c = 1;
            for (IPlayer player : gameResult.getSortedResult()) {
                playerPoints.add(new PlayerPoints(player.getName(), player.getPlayerId(), player.getTotalPoints()));
                log.info("{}. {} - {} pts", c++, player.getName(), player.getTotalPoints());
            }
        }

        TournamentEndedEvent tee = new TournamentEndedEvent(
                winnerPlayerId,
                lastGame.getGame().getGameId(),
                playerPoints,
                tournamentName,
                tournamentId);

        playerManager.toSet().forEach( player -> {
            player.onTournamentEnded(tee);
        });

        InternalGameEvent gevent = new InternalGameEvent(
                System.currentTimeMillis(),
                tee);
        globalEventBus.post(gevent);
        globalEventBus.post(gevent.getGameMessage());
    }

    private void organizePlayersInLevel() {

        log.info("Organizing players in Level. Current level: {}, noof levels: {}", currentLevel, tournamentPlan.getLevels().size());
        if (isTournamentComplete()) {
            completeTournament();
            return;
        }

        TournamentLevel tLevel = tournamentPlan.getLevelAt(currentLevel);

        if (currentLevel != 0) {
            playersStillInTournament.clear();
            TournamentLevel previousLevel = tournamentPlan.getLevelAt(currentLevel-1); // Might be wrong index here
            playersStillInTournament.addAll(previousLevel.getPlayersAdvancing());
        }

        Set<IPlayer> playersInTournament = playersStillInTournament.toSet();

        tLevel.setPlayers(playersInTournament);
        for (TournamentPlannedGame tGame : tLevel.getPlannedGames()) {

            Set<IPlayer> players = TournamentUtil.getRandomPlayers(playersInTournament, tGame.getExpectedNoofPlayers());
            log.info("adding noof players to new game: {}", players.size());
            if (players.size() == 0) {
                log.error("Hoa, got 0 players to add to game...");
                continue;
            }
            tGame.setPlayers(players);
            playersInTournament.removeAll(players);

            Game game = gameManager.createGame(gameFeatures);
            game.setOutgoingEventBus(outgoingEventBus);
            tGame.setGame(game);
            players.forEach(player -> {
                game.addPlayer(player);
            });
            games.put(game.getGameId(), game);
        }

        publishTournamentPlan();
    }

    public void planTournament() {
        tournamentPlan = new TournamentPlan(gameFeatures, playerManager);
        publishTournamentPlan();
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

        if (playerManager.containsPlayerWithName(player.getName())) {
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
        String gameId = registerMove.getGameId();

        Game game = games.get(gameId);
        if (game != null) {
            game.registerMove(registerMove);
        }
    }

    @Subscribe
    public void onInternalGameEvent(InternalGameEvent internalGameEvent) {
        if (internalGameEvent.getGameMessage() instanceof GameEndedEvent) {
            GameEndedEvent gee = (GameEndedEvent)internalGameEvent.getGameMessage();
            if (games.containsKey(gee.getGameId())) {
                log.info("GameId: {} ended.", gee.getGameId());
                if (areAllGamesInLevelComplete(currentLevel)) {
                    currentLevel++;
                    organizePlayersInLevel();
                } else {
                    publishTournamentPlan();
                }
            }
        }
    }

    private boolean areAllGamesInLevelComplete(int level) {
        if (!isTournamentStarted()) {
            return false;
        }

        TournamentLevel tLevel = tournamentPlan.getLevelAt(level);

        for (TournamentPlannedGame tGame : tLevel.getPlannedGames()) {
            if (!tGame.getGame().isEnded()) {
                log.info("Tournament level: {} is not complete.", level);
                return false;
            }
        }
        log.info("Tournament level: {} is complete.", level);
        return true;
    }

    private void addPlayer(IPlayer player) {
        playerManager.add(player);

        planTournament();
    }

    private void removePlayer(IPlayer player) {
        playerManager.remove(player);

        planTournament();
    }

    public void startTournament() {

        if (tournamentStarted) {
            return;
        }

        // ToDo: Not thread safe
        tournamentStarted = true;

        playersStillInTournament.clear();
        playersStillInTournament.addAll(playerManager.getLivePlayers());
        organizePlayersInLevel();
    }

    public void playerLostConnection(String playerId) {

        IPlayer player = playerManager.getPlayer(playerId);
        if (player == null) {
            return;
        }

        if (isTournamentStarted()) {
            player.lostConnection(-1);
        } else {
            removePlayer(player);
        }
    }

    public void publishTournamentPlan() {
        globalEventBus.post(getTournamentPlan());
    }

    public TournamentGamePlan getTournamentPlan() {
        return TournamentPlanConverter.getTournamentPlan(
                tournamentPlan,
                tournamentName,
                tournamentId
        );
    }

    public GameSettings getGameSettings() {
        return GameSettingsConverter.toGameSettings(
                gameFeatures
        );
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
        if (!isTournamentStarted()) {
            this.gameFeatures = gameFeatures;
            planTournament();
        }
    }

    public GameFeatures getGameFeatures() {
        return gameFeatures;
    }


}
