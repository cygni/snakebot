package se.cygni.snake.game;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.game.Coordinate;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.transformation.AddWorldObjectAtRandomPosition;
import se.cygni.game.transformation.RemoveRandomWorldObject;
import se.cygni.game.transformation.RemoveSnake;
import se.cygni.game.worldobject.Food;
import se.cygni.game.worldobject.Obstacle;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.snake.api.model.DeathReason;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.player.IPlayer;

import java.util.*;
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
        world = new WorldState(gameFeatures.width, gameFeatures.height);

        // Place players
        game.getPlayers().stream().forEach( player -> {
            SnakeHead snakeHead = new SnakeHead(player.getName(), player.getPlayerId(), 0);
            AddWorldObjectAtRandomPosition randomPosition = new AddWorldObjectAtRandomPosition(snakeHead);
            world = randomPosition.transform(world);
        });

        game.getPlayers().stream().forEach( player -> {
            player.onGameStart(game.getGameId(), game.getNoofPlayers(), world.getWidth(), world.getHeight());
        });

        InternalGameEvent gevent = new InternalGameEvent(System.currentTimeMillis());
        gevent.onGameStart(game.getGameId(), game.getNoofPlayers(), world.getWidth(), world.getHeight());
        globalEventBus.post(gevent);
    }

    private void gameLoop() {
        initSnakeDirections();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // Loop till winner is decided
                while (isGameRunning()) {

                    countDownLatch = new CountDownLatch(game.getLivePlayers().size());
                    registerMoveQueue = new ConcurrentLinkedQueue<>();

                    Set<IPlayer> players = game.getPlayers();
                    game.getLivePlayers().stream().forEach( player -> {
                        player.onWorldUpdate(
                                world, game.getGameId(), currentWorldTick, players
                        );
                    });

                    InternalGameEvent gevent = new InternalGameEvent(System.currentTimeMillis());
                    gevent.onWorldUpdate(world, game.getGameId(), currentWorldTick, game.getPlayers());
                    globalEventBus.post(gevent);

                    long tstart = System.currentTimeMillis();
                    try {
                        countDownLatch.await(gameFeatures.timeInMsPerTick, TimeUnit.MILLISECONDS);
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
                    if (gameFeatures.foodEnabled) {
                        randomFood();
                    }

                    if (gameFeatures.obstaclesEnabled) {
                        randomObstacle();
                    }
                }

                Set<IPlayer> players = game.getPlayers();
                game.getPlayers().stream().forEach( player -> {
                    player.onGameEnded(
                            getLeaderPlayerId(),
                            game.getGameId(),
                            currentWorldTick,
                            world,
                            players
                    );
                });

                InternalGameEvent gevent = new InternalGameEvent(System.currentTimeMillis());
                gevent.onGameEnded(getLeaderPlayerId(),
                        game.getGameId(),
                        currentWorldTick,
                        world,
                        game.getPlayers());
                globalEventBus.post(gevent);
                globalEventBus.post(gevent.getGameMessage());
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    private List<SnakeHead> getSortedSnakeHeads() {
        ArrayList<SnakeHead> sortedHeads = new ArrayList<>();
        Map<String, SnakeHead> snakeHeads = new HashMap<>();

        int[] positions = world.listPositionsWithContentOf(SnakeHead.class);
        for (int pos : positions) {
            SnakeHead sh = (SnakeHead)world.getTile(pos).getContent();
            snakeHeads.put(sh.getPlayerId(), sh);
        }

        for (String playerId : registerMoveQueue) {
            sortedHeads.add(snakeHeads.get(playerId));
        }

        // Add any remaining SnakeHeads
        if (sortedHeads.size() != positions.length) {
            for (int pos : positions) {
                SnakeHead sh = (SnakeHead)world.getTile(pos).getContent();
                if (!sortedHeads.contains(sh))
                    sortedHeads.add(sh);
            }
        }

        return sortedHeads;
    }

    private void randomObstacle() {
        if (gameFeatures.removeObstacleLikelihood > (Math.random()*100.0)) {
            RemoveRandomWorldObject<Obstacle> removeTransform =
                    new RemoveRandomWorldObject<>(Obstacle.class);
            world = removeTransform.transform(world);
        }

        if (gameFeatures.addObstacleLikelihood > (Math.random()*100.0)) {
            AddWorldObjectAtRandomPosition addTransform =
                    new AddWorldObjectAtRandomPosition(new Obstacle());
            world = addTransform.transform(world);
        }
    }


    private void randomFood() {
        if (gameFeatures.removeFoodLikelihood > (Math.random()*100.0)) {
            RemoveRandomWorldObject<Food> removeTransform =
                    new RemoveRandomWorldObject<>(Food.class);
            world = removeTransform.transform(world);
        }

        if (gameFeatures.addFoodLikelihood > (Math.random()*100.0)) {
            AddWorldObjectAtRandomPosition addTransform =
                    new AddWorldObjectAtRandomPosition(new Food());
            world = addTransform.transform(world);
        }
    }

    private boolean spontaneousGrowth() {
        if (gameFeatures.spontaneousGrowthEveryNWorldTick > 0) {
            return currentWorldTick % gameFeatures.spontaneousGrowthEveryNWorldTick == 0;
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

    private void snakeDied(SnakeHead head, DeathReason deathReason, int position) {
        RemoveSnake remove = new RemoveSnake(head);
        try {
            world = remove.transform(world);
        } catch (Exception e) { e.printStackTrace(); }
        game.getPlayer(head.getPlayerId()).dead();
        snakeDirections.remove(head.getPlayerId());

        Coordinate coordinate = world.translatePosition(position);

        game.getPlayers().stream().forEach( player -> {
            player.onPlayerDied(
                    deathReason,
                    head.getPlayerId(),
                    coordinate.getX(), coordinate.getY(),
                    game.getGameId(), currentWorldTick
            );
        });

        InternalGameEvent gevent = new InternalGameEvent(System.currentTimeMillis());
        gevent.onPlayerDied(deathReason,
                head.getPlayerId(),
                coordinate.getX(), coordinate.getY(),
                game.getGameId(), currentWorldTick
        );
        globalEventBus.post(gevent);
    }
}
