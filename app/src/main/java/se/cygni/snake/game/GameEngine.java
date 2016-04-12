package se.cygni.snake.game;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.transformation.AddWorldObjectAtRandomPosition;
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

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final Game game;
    private WorldState world;
    private long currentWorldTick = 0;
    private java.util.Map<String, Direction> snakeDirections;
    private AtomicBoolean allowedToRun = new AtomicBoolean(false);
    private final EventBus globalEventBus;
    private final WorldTransformer worldTransformer;

    private CountDownLatch countDownLatch;
    private ConcurrentLinkedQueue<String> registerMoveQueue;


    public GameEngine(GameFeatures gameFeatures, Game game, EventBus globalEventBus) {
        this.gameFeatures = gameFeatures;
        this.game = game;
        this.globalEventBus = globalEventBus;
        this.worldTransformer = new WorldTransformer(game);
    }

    public void reApplyGameFeatures(GameFeatures gameFeatures) {
        this.gameFeatures = gameFeatures;
    }

    public void startGame() {
        allowedToRun.set(true);
        initGame();
        gameLoop();
    }

    public void abort() {
        allowedToRun.set(false);
    }

    private void initGame() {
        world = new WorldState(gameFeatures.getWidth(), gameFeatures.getHeight());

        // Place players
        game.getPlayers().stream().forEach( player -> {
            SnakeHead snakeHead = new SnakeHead(player.getName(), player.getPlayerId(), 0);
            AddWorldObjectAtRandomPosition randomPosition = new AddWorldObjectAtRandomPosition(snakeHead);
            world = randomPosition.transform(world);
        });

        GameStartingEvent gameStartingEvent = new GameStartingEvent(
                game.getGameId(),
                game.getNoofPlayers(),
                world.getWidth(), world.getHeight());

        game.getPlayers().stream().forEach( player -> {
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
                // Loop till winner is decided
                while (isGameRunning()) {

                    Set<IPlayer> livePlayers = game.getLivePlayers();
                    countDownLatch = new CountDownLatch(livePlayers.size());
                    registerMoveQueue = new ConcurrentLinkedQueue<>();

                    DecrementTailProtection decrementTailProtection = new DecrementTailProtection();
                    world = decrementTailProtection.transform(world);

                    Set<IPlayer> players = game.getPlayers();
                    MapUpdateEvent mapUpdateEvent = GameMessageConverter
                            .onWorldUpdate(world, game.getGameId(), currentWorldTick, players);

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
                    log.info("GameId: {}, tick: {}, time waiting: " + timeSpent + "ms", game.getGameId(), currentWorldTick);

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

                // Game is Over, assign points to last man standing
                Set<IPlayer> livingPlayers = game.getLivePlayers();

                for (IPlayer player : livingPlayers) {
                    player.addPoints(
                            PointReason.LAST_SNAKE_ALIVE,
                            gameFeatures.getPointsLastSnakeLiving());
                }

                // Notify of GameEnded
                Set<IPlayer> players = game.getPlayers();
                GameEndedEvent gameEndedEvent = GameMessageConverter.onGameEnded(
                        getLeaderPlayerId(),
                        game.getGameId(),
                        currentWorldTick,
                        world,
                        players
                );

                game.getPlayers().stream().forEach( player -> {
                    player.onGameEnded(gameEndedEvent);
                });

                InternalGameEvent gevent = new InternalGameEvent(
                        System.currentTimeMillis(),
                        gameEndedEvent);
                globalEventBus.post(gevent);
                globalEventBus.post(gevent.getGameMessage());
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    private void randomObstacle() {
        if (gameFeatures.getRemoveObstacleLikelihood() > (Math.random()*100.0)) {
            RemoveRandomWorldObject<Obstacle> removeTransform =
                    new RemoveRandomWorldObject<>(Obstacle.class);
            world = removeTransform.transform(world);
        }

        if (gameFeatures.getAddObstacleLikelihood() > (Math.random()*100.0)) {
            AddWorldObjectAtRandomPosition addTransform =
                    new AddWorldObjectAtRandomPosition(new Obstacle());
            world = addTransform.transform(world);
        }
    }


    private void randomFood() {
        if (gameFeatures.getRemoveFoodLikelihood() > (Math.random()*100.0)) {
            RemoveRandomWorldObject<Food> removeTransform =
                    new RemoveRandomWorldObject<>(Food.class);
            world = removeTransform.transform(world);
        }

        if (gameFeatures.getAddFoodLikelihood() > (Math.random()*100.0)) {
            AddWorldObjectAtRandomPosition addTransform =
                    new AddWorldObjectAtRandomPosition(new Food());
            world = addTransform.transform(world);
        }
    }

    private boolean spontaneousGrowth() {
        if (gameFeatures.getSpontaneousGrowthEveryNWorldTick() > 0) {
            return currentWorldTick % gameFeatures.getSpontaneousGrowthEveryNWorldTick() == 0;
        }
        return false;
    }

    private void initSnakeDirections() {
        snakeDirections = new HashMap<>();

        game.getPlayers().stream().forEach( player -> {
            snakeDirections.put(player.getPlayerId(), getRandomDirection());
        });
    }

    public boolean isGameRunning() {
        return (allowedToRun.get() &&
                game.getLiveAndRemotePlayers().size() > 0 &&
                noofLiveSnakesInWorld() > 1);

    }

    public int noofLiveSnakesInWorld() {
        return (int) game.getPlayers().stream()
                .filter(player -> player.isAlive())
                .count();
    }

    public String getLeaderPlayerId() {
        IPlayer winner = game.getPlayers().stream()
                .max((player1, player2) -> {
                    return Integer.compare(
                            player1.getTotalPoints(),
                            player2.getTotalPoints());
                })
                .get();

        return winner.getPlayerId();
    }

    public void registerMove(long gameTick, String playerId, Direction direction) {
        // Todo: Possible sync problem here if a players registers move before game has actually started countDownLatch may be null.
        // Todo: Must handle misbehaving clients that may send more than one registerMove per world tick!
        if (gameTick == currentWorldTick) {
            registerMoveQueue.add(playerId);
            snakeDirections.put(playerId, direction);
            countDownLatch.countDown();
        } else {
            log.info("Player with id {} too late in registering move", playerId);
        }
    }

    private Direction getRandomDirection() {
        int max = Direction.values().length-1;
        Random r = new Random();
        return Direction.values()[r.nextInt(max)];
    }
}
