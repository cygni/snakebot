package se.cygni.snake.game;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.random.XORShiftRandom;
import se.cygni.game.transformation.AddWorldObjectAtRandomPosition;
import se.cygni.game.transformation.AddWorldObjectsInCircle;
import se.cygni.game.transformation.DecrementTailProtection;
import se.cygni.game.transformation.RemoveRandomWorldObject;
import se.cygni.game.worldobject.Food;
import se.cygni.game.worldobject.Obstacle;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.PointReason;
import se.cygni.snake.apiconversion.GameMessageConverter;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.player.IPlayer;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * GameEngine is responsible for:
 *
 * - Maintaining the world
 * - Handling the time ticker
 * - Executing player moves
 * - Executing the rules from GameFeatures
 */
public class GameEngine {
    private static Logger log = LoggerFactory
            .getLogger(GameEngine.class);

    private GameFeatures gameFeatures;
    private WorldState world;
    private long currentWorldTick = 0;
    private java.util.Map<String, Direction> snakeDirections;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicBoolean gameComplete = new AtomicBoolean(false);
    private final EventBus globalEventBus;
    private final WorldTransformer worldTransformer;
    private final PlayerManager playerManager;
    private final String gameId;

    private CountDownLatch countDownLatch;
    private Set<String> registeredMovesByPlayers = Collections.synchronizedSet(new HashSet<>());
    private XORShiftRandom random = new XORShiftRandom();
    private GameResult gameResult;

    public GameEngine(GameFeatures gameFeatures,
                      PlayerManager playerManager,
                      String gameId,
                      EventBus globalEventBus) {

        this.gameFeatures = gameFeatures;
        this.gameId = gameId;
        this.playerManager = playerManager;
        this.globalEventBus = globalEventBus;
        this.worldTransformer = new WorldTransformer(
                gameFeatures, playerManager, gameId, globalEventBus
        );
        this.gameResult = new GameResult();
    }

    public void reApplyGameFeatures(GameFeatures gameFeatures) {
        this.gameFeatures = gameFeatures;
    }

    public void startGame() {
        initGame();
        gameLoop();
    }

    public void abort() {
        isRunning.set(false);
    }

    private void initGame() {
        world = new WorldState(gameFeatures.getWidth(), gameFeatures.getHeight());

        // Place players
        List<SnakeHead> snakeHeads = playerManager.toSet().stream().map(player -> new SnakeHead(player.getName(), player.getPlayerId(), 0)).collect(Collectors.toList());
        AddWorldObjectsInCircle circleFormation = new AddWorldObjectsInCircle(snakeHeads, 0.9d);
        world = circleFormation.transform(world);
        GameStartingEvent gameStartingEvent = new GameStartingEvent(
                gameId,
                playerManager.size(),
                world.getWidth(), world.getHeight());

        playerManager.toSet().stream().forEach( player -> {
            player.onGameStart(gameStartingEvent);
        });

        InternalGameEvent gevent = new InternalGameEvent(System.currentTimeMillis(),
                gameStartingEvent);
        globalEventBus.post(gevent);
    }

    private void gameLoop() {
        initSnakeDirections();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // Set internal state to running
                isRunning.set(true);

                // Loop till winner is decided
                while (isGameRunning()) {

                    Set<IPlayer> livePlayers = playerManager.getLivePlayers();
                    countDownLatch = new CountDownLatch(livePlayers.size());
                    registeredMovesByPlayers.clear();

                    DecrementTailProtection decrementTailProtection = new DecrementTailProtection();
                    world = decrementTailProtection.transform(world);

                    Set<IPlayer> players = playerManager.toSet();
                    MapUpdateEvent mapUpdateEvent = GameMessageConverter
                            .onWorldUpdate(world, gameId, currentWorldTick, players);

                    livePlayers.stream().forEach( player -> {
                        player.onWorldUpdate(mapUpdateEvent);
                    });

                    InternalGameEvent gevent = new InternalGameEvent(
                            System.currentTimeMillis(), mapUpdateEvent);
                    globalEventBus.post(gevent);

                    long tstart = System.currentTimeMillis();
                    try {
                        countDownLatch.await(gameFeatures.getTimeInMsPerTick(), TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    long timeSpent = System.currentTimeMillis() - tstart;
                    log.info("GameId: {}, tick: {}, time waiting: " + timeSpent + "ms", gameId, currentWorldTick);

                    try {
                        world = worldTransformer.transform(snakeDirections, gameFeatures, world, spontaneousGrowth(), currentWorldTick);
                    } catch (Exception e) {
                        // This is really undefined, if this happens we have a bug
                        log.error("Bug found in WorldTransformer:", e);
                    }

                    currentWorldTick++;

                    // Add random objects
                    if (gameFeatures.isFoodEnabled()) {
                        randomFood();
                    }

                    if (gameFeatures.isObstaclesEnabled()) {
                        randomObstacle();
                    }
                }

                // Set internal state to not running
                isRunning.set(false);

                // Game is Over, assign points to last man standing
                Set<IPlayer> livingPlayers = playerManager.getLivePlayers();

                for (IPlayer player : livingPlayers) {
                    player.addPoints(
                            PointReason.LAST_SNAKE_ALIVE,
                            gameFeatures.getPointsLastSnakeLiving());
                }

                // Create GameResult
                Set<IPlayer> allPlayers = playerManager.toSet();
                for (IPlayer player : allPlayers) {
                    gameResult.addResult(player.getTotalPoints(), player);
                }

                gameComplete.set(true);

                // Notify of GameEnded
                Set<IPlayer> players = playerManager.toSet();
                GameEndedEvent gameEndedEvent = GameMessageConverter.onGameEnded(
                        gameResult.getWinner().getPlayerId(),
                        gameId,
                        currentWorldTick,
                        world,
                        players
                );

                players.stream().forEach( player -> {
                    player.onGameEnded(gameEndedEvent);
                });

                InternalGameEvent gevent = new InternalGameEvent(
                        System.currentTimeMillis(),
                        gameEndedEvent);
                globalEventBus.post(gevent);
                globalEventBus.post(gevent.getGameMessage());

                publishGameChanged();
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    private void randomObstacle() {
        if (shouldExecute(gameFeatures.getRemoveObstacleLikelihood())) {
            RemoveRandomWorldObject<Obstacle> removeTransform =
                    new RemoveRandomWorldObject<>(Obstacle.class);
            world = removeTransform.transform(world);
        }

        if (shouldExecute(gameFeatures.getAddObstacleLikelihood())) {
            AddWorldObjectAtRandomPosition addTransform =
                    new AddWorldObjectAtRandomPosition(new Obstacle());
            world = addTransform.transform(world);
        }
    }

    private void randomFood() {
        if (shouldExecute(gameFeatures.getRemoveFoodLikelihood())) {
            RemoveRandomWorldObject<Food> removeTransform =
                    new RemoveRandomWorldObject<>(Food.class);
            world = removeTransform.transform(world);
        }

        if (shouldExecute(gameFeatures.getAddFoodLikelihood())) {
            AddWorldObjectAtRandomPosition addTransform =
                    new AddWorldObjectAtRandomPosition(new Food());
            world = addTransform.transform(world);
        }
    }

    /**
     *
     * @param likelihood
     * @return true if a random double * 100 is smaller than likelihood
     */
    private boolean shouldExecute(int likelihood) {
        return likelihood > random.nextDouble()*100;
    }

    private boolean spontaneousGrowth() {
        if (gameFeatures.getSpontaneousGrowthEveryNWorldTick() > 0) {
            return currentWorldTick % gameFeatures.getSpontaneousGrowthEveryNWorldTick() == 0;
        }
        return false;
    }

    private void initSnakeDirections() {
        snakeDirections = new HashMap<>();

        playerManager.toSet().stream().forEach( player -> {
            snakeDirections.put(player.getPlayerId(), getRandomDirection());
        });
    }

    public boolean isGameRunning() {
        return (isRunning.get() &&
                playerManager.getLiveAndRemotePlayers().size() > 0 &&
                noofLiveSnakesInWorld() > 1);
    }

    public int noofLiveSnakesInWorld() {
        return (int) playerManager.toSet().stream()
                .filter(player -> player.isAlive())
                .count();
    }

    public void registerMove(long gameTick, String playerId, Direction direction) {
        if (!isGameRunning()) {
            return;
        }

        // Move is for wrong gameTick
        if (gameTick != currentWorldTick) {
            log.warn("Player: {} with id {} sent move within wrong world tick. Current world tick: {}, player's world tick: {}",
                    playerManager.getPlayerName(playerId), playerId,
                    currentWorldTick, gameTick);
            return;
        }

        // Player has already registered a move
        if (registeredMovesByPlayers.contains(playerId)) {
            log.warn("Player: {} with id {} sent more than one move. Current world tick: {}, player's world tick: {}",
                    playerManager.getPlayerName(playerId), playerId,
                    currentWorldTick, gameTick);
            return;
        }

        registeredMovesByPlayers.add(playerId);
        snakeDirections.put(playerId, direction);
        countDownLatch.countDown();
    }

    private Direction getRandomDirection() {
        int max = Direction.values().length-1;
        return Direction.values()[random.nextInt(max)];
    }

    public boolean isGameComplete() {
        return gameComplete.get();
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public void publishGameChanged() {
        InternalGameEvent gevent = new InternalGameEvent(System.currentTimeMillis());
        gevent.onGameChanged(gameId);
        globalEventBus.post(gevent);
    }

    public long getCurrentWorldTick() {
        return currentWorldTick;
    }
}
