package se.cygni.snake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.api.event.*;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.model.PlayerPoints;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.api.util.GameSettingsUtils;
import se.cygni.snake.client.AnsiPrinter;
import se.cygni.snake.client.BaseSnakeClient;
import se.cygni.snake.client.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExampleSnakePlayer extends BaseSnakeClient {

    private static Logger log = LoggerFactory
            .getLogger(ExampleSnakePlayer.class);

    Random random = new Random();

    private AnsiPrinter ansiPrinter;
    private String name = "#emil_" + random.nextInt(1000);
    private String host = "localhost";
    private int port = 8080;
//    private String host = "snake.cygni.se";
//    private int port = 80;
    private GameMode gameMode = GameMode.TOURNAMENT;

    public static void main(String[] args) {

        Runnable task = () -> {

            ExampleSnakePlayer sp = new ExampleSnakePlayer();
            sp.connect();

            // Keep this process alive as long as the
            // Snake is connected and playing.
            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (sp.isPlaying());

            log.info("Shutting down");
        };


        Thread thread = new Thread(task);
        thread.start();
    }

    public ExampleSnakePlayer() {
        ansiPrinter = new AnsiPrinter(true);
    }

    @Override
    public void onMapUpdate(MapUpdateEvent mapUpdateEvent) {
        ansiPrinter.printMap(mapUpdateEvent);

        // MapUtil contains lot's of useful methods for querying the map!
        MapUtil mapUtil = new MapUtil(mapUpdateEvent.getMap(), getPlayerId());


        List<SnakeDirection> directions = new ArrayList<>();

        // Let's see in which directions I can move
        if (mapUtil.canIMoveInDirection(SnakeDirection.LEFT))
            directions.add(SnakeDirection.LEFT);
        if (mapUtil.canIMoveInDirection(SnakeDirection.RIGHT))
            directions.add(SnakeDirection.RIGHT);
        if (mapUtil.canIMoveInDirection(SnakeDirection.UP))
            directions.add(SnakeDirection.UP);
        if (mapUtil.canIMoveInDirection(SnakeDirection.DOWN))
            directions.add(SnakeDirection.DOWN);

        Random r = new Random();
        SnakeDirection chosenDirection = SnakeDirection.DOWN;

        // Choose a random direction
        if (!directions.isEmpty())
            chosenDirection = directions.get(r.nextInt(directions.size()));

        // Register action here!
        registerMove(mapUpdateEvent.getGameTick(), chosenDirection);
    }

    @Override
    public void onInvalidPlayerName(InvalidPlayerName invalidPlayerName) {

    }

    @Override
    public void onSnakeDead(SnakeDeadEvent snakeDeadEvent) {
        log.info("A snake {} died by {}",
                snakeDeadEvent.getPlayerId(),
                snakeDeadEvent.getDeathReason() + " at tick: " + snakeDeadEvent.getGameTick());
    }

    @Override
    public void onTournamentEnded(TournamentEndedEvent tournamentEndedEvent) {
        log.info("TournamentInfo has ended, winner playerId: {}", tournamentEndedEvent.getPlayerWinnerId());
        int c = 1;
        for (PlayerPoints pp : tournamentEndedEvent.getGameResult()) {
            log.info("{}. {} - {} points", c++, pp.getName(), pp.getPoints());
        }
    }

    @Override
    public void onGameEnded(GameEndedEvent gameEndedEvent) {
        log.info("{} GameEnded gameId: {}, at tick: {}, winner: {}",
                getName(),
                gameEndedEvent.getGameId(),
                gameEndedEvent.getGameTick(),
                gameEndedEvent.getPlayerWinnerId());
    }

    @Override
    public void onGameStarting(GameStartingEvent gameStartingEvent) {
        log.info("GameStartingEvent, gameId: {} ", gameStartingEvent.getGameId());
    }

    @Override
    public void onPlayerRegistered(PlayerRegistered playerRegistered) {
        log.info("PlayerRegistered: " + playerRegistered);

        // Disable this if you want to start the game manually from
        // the web GUI
        startGame();
    }

    @Override
    public void onGameLink(GameLinkEvent gameLinkEvent) {
        log.info("The game can be viewed at: {}", gameLinkEvent.getUrl());
    }

    @Override
    public void onSessionClosed() {
        log.info("Session closed");
    }

    @Override
    public void onConnected() {
        log.info("Connected as: {}, registering for {}...", getName(), gameMode);
        GameSettings gameSettings = GameSettingsUtils.trainingWorld();
        gameSettings.setStartObstacles(10);
        registerForGame(gameSettings);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getServerHost() {
        return host;
    }

    @Override
    public int getServerPort() {
        return port;
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }
}