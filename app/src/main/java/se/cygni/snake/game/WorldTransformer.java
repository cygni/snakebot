package se.cygni.snake.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import se.cygni.game.Coordinate;
import se.cygni.game.Tile;
import se.cygni.game.TileMultipleContent;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.exception.ObstacleCollision;
import se.cygni.game.exception.SnakeCollision;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.exception.WallCollision;
import se.cygni.game.transformation.KeepOnlyObjectsOfType;
import se.cygni.game.transformation.KeepOnlySnakeWithId;
import se.cygni.game.transformation.MoveSnake;
import se.cygni.game.transformation.TailNibbled;
import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.Food;
import se.cygni.game.worldobject.Obstacle;
import se.cygni.game.worldobject.SnakeBody;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.game.worldobject.WorldObject;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.model.DeathReason;
import se.cygni.snake.api.model.PointReason;
import se.cygni.snake.apiconversion.GameMessageConverter;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.player.IPlayer;

public class WorldTransformer {

    private static final Logger log = LoggerFactory.getLogger(WorldTransformer.class);

    private final GameFeatures gameFeatures;
    private final PlayerManager playerManager;
    private final String gameId;
    private final EventBus globalEventBus;
    private int snakesDiedThisRound = 0;
    private final ThreadLocal<WorldState> startingWorldState = new ThreadLocal<>();

    public WorldTransformer(GameFeatures gameFeatures, PlayerManager playerManager, String gameId,
                            EventBus globalEventBus) {
        this.gameFeatures = gameFeatures;
        this.playerManager = playerManager;
        this.gameId = gameId;
        this.globalEventBus = globalEventBus;
    }

    private List<WorldState> analyzeWorldStates(List<WorldState> worldStates, GameFeatures gameFeatures,
                                                long worldTick) {

        // Create a map of which states each snake is responsible for
        final Map<String, WorldState> snakeToWorldState = new HashMap<>();
        for (final WorldState ws : worldStates) {

            snakeToWorldState.put(ws.listSnakeIds().get(0), ws);
        }

        // Create a merged view of all states
        TileMultipleContent[] mergedTileContent = mergeStates(worldStates);

        // Find snake heads that have passed through each other. This is
        // immediate death. Example:
        // | B1 | H1 | H2 | B2 | (H2 moves left, H1 moves right)
        // results in:
        // | | B1 H2 | H1 B2 | |
        final Set<String> snakeHeadsPassingThrough = getHeadsPassingThrough(mergedTileContent);
        mergedTileContent = snakeDied(snakeToWorldState, snakeHeadsPassingThrough, DeathReason.CollisionWithSnake,
                worldTick);

        if (snakeToWorldState.isEmpty()) {
            return snakeToWorldState.values().stream().collect(Collectors.toList());
        }

        // Find snakes overlapping on more than one tile,
        // this kills both of them.
        final Set<String> overlappingSnakes = getOverlappingSnakes(mergedTileContent);
        mergedTileContent = snakeDied(snakeToWorldState, overlappingSnakes, DeathReason.CollisionWithSnake, worldTick);

        if (snakeToWorldState.isEmpty()) {
            return snakeToWorldState.values().stream().collect(Collectors.toList());
        }

        // Validate all tiles
        for (final TileMultipleContent tile : mergedTileContent) {
            if (!tile.isValidCombinationOfContents()) {

                // Special case when head hits tail and that feature is enabled
                if (gameFeatures.isHeadToTailConsumes() && isNibbleSituation(tile, snakeToWorldState)) {
                    handleTailNibbling(gameFeatures, snakeToWorldState, tile);
                    continue;
                }

                // Remove all WorldStates with SnakesHeads involving this clash
                snakeDied(snakeToWorldState, tile.listOffendingSnakeHeadIds(), DeathReason.CollisionWithSnake,
                        worldTick);

                // Assign points to SnakeParts still living
                final List<SnakeBody> survivors = tile.listContentsOfType(SnakeBody.class);
                survivors.stream().forEach(snakeBody -> {
                    playerManager.getPlayer(snakeBody.getPlayerId()).addPoints(PointReason.CAUSED_SNAKE_DEATH,
                            gameFeatures.getPointsPerCausedDeath());
                });
            }
        }

        return snakeToWorldState.values().stream().collect(Collectors.toList());
    }

    private TileMultipleContent[] convertToTileMultipleContent(Tile[] tiles) {
        final TileMultipleContent[] mTiles = new TileMultipleContent[tiles.length];
        IntStream.range(0, tiles.length).forEach(pos -> {
            final WorldObject wo = tiles[pos].getContent();
            mTiles[pos] = new TileMultipleContent(wo);
        });
        return mTiles;
    }

    private Tile[] createValidWorldState(TileMultipleContent[] mTiles) {
        final Tile[] tiles = new Tile[mTiles.length];

        IntStream.range(0, mTiles.length).forEach(pos -> {
            if (!mTiles[pos].isValidCombinationOfContents()) {
                throw new IllegalStateException("Tried to create valid world state from invalid merge of states");
            }

            tiles[pos] = new Tile(mTiles[pos].getContent());
        });
        return tiles;
    }

    // Get all possible world states (one per snake + static objects)
    private List<WorldState> getAllPossibleWorldStates(WorldState ws, Map<String, Direction> directions,
                                                       boolean spontaneousGrowth, long worldTick) throws TransformationException {

        final List<WorldState> worldStates = new ArrayList<>();

        // One world for each Snake
        final int[] headPositions = ws.listPositionsWithContentOf(SnakeHead.class);
        for (final int headPosition : headPositions) {

            final SnakeHead snakeHead = (SnakeHead) ws.getTile(headPosition).getContent();
            final String playerId = snakeHead.getPlayerId();

            // First remove all other objects (just keep the current snake)
            final KeepOnlySnakeWithId keepSnake = new KeepOnlySnakeWithId(playerId);
            final WorldState singleSnakeState = keepSnake.transform(ws);

            // Transform the snake in the choosen direction
            final Direction snakeDirection = directions.get(playerId);
            final MoveSnake moveSnake = new MoveSnake(singleSnakeState.getSnakeHeadForBodyAt(headPosition),
                    snakeDirection, spontaneousGrowth);

            try {
                final WorldState nws = moveSnake.transform(singleSnakeState);

                // Assign points
                if (moveSnake.isFoodConsumed()) {
                    playerManager.getPlayer(playerId).addPoints(PointReason.FOOD, gameFeatures.getPointsPerFood());
                }

                if (moveSnake.isGrowthExecuted()) {
                    playerManager.getPlayer(playerId).addPoints(PointReason.GROWTH, gameFeatures.getPointsPerLength());
                }

                worldStates.add(nws);

            } catch (final ObstacleCollision oc) {
                notifySnakeDied(snakeHead, DeathReason.CollisionWithObstacle, ws.translatePosition(oc.getPosition()),
                        worldTick);
            } catch (final WallCollision wc) {
                notifySnakeDied(snakeHead, DeathReason.CollisionWithWall, ws.translatePosition(wc.getPosition()),
                        worldTick);
            } catch (final SnakeCollision sc) {
                notifySnakeDied(snakeHead, DeathReason.CollisionWithSelf, ws.translatePosition(sc.getPosition()),
                        worldTick);
            } catch (final TransformationException oc) {
                notifySnakeDied(snakeHead, DeathReason.CollisionWithObstacle, ws.translatePosition(0), worldTick);
            }
        }

        return worldStates;
    }

    private Set<String> getHeadsPassingThrough(TileMultipleContent[] tiles) {
        final Set<String> passingThroughHeads = new HashSet<>();

        // Find any two snake heads that have switched places.
        final WorldState startState = startingWorldState.get();

        final Map<String, Integer> newSnakeHeadPositions = new HashMap<>();
        final Map<Integer, List<String>> newPositionsPerSnakeHead = new HashMap<>();

        for (final TileMultipleContent tile : tiles) {
            final List<SnakeHead> heads = tile.listContentsOfType(SnakeHead.class);
            for (final SnakeHead head : heads) {
                newSnakeHeadPositions.put(head.getPlayerId(), head.getPosition());

                final List<String> ids = newPositionsPerSnakeHead.getOrDefault(head.getPosition(), new ArrayList<>());
                ids.add(head.getPlayerId());
                newPositionsPerSnakeHead.put(head.getPosition(), ids);
            }
        }

        startState.listSnakeIds().stream().forEach(snakeId -> {
            final int originalHeadPosition = startState.getPositionOfSnakeHead(snakeId);

            // The snake might have died already
            if (newSnakeHeadPositions.containsKey(snakeId)) {
                final int newHeadPosition = newSnakeHeadPositions.get(snakeId);

                final List<String> ids = newPositionsPerSnakeHead.getOrDefault(originalHeadPosition, new ArrayList<>());
                for (final String newHead : ids) {
                    if (newSnakeHeadPositions.get(newHead) == originalHeadPosition
                            && newHeadPosition == startState.getPositionOfSnakeHead(newHead)) {
                        passingThroughHeads.add(newHead);
                        passingThroughHeads.add(snakeId);
                    }
                }
            }
        });

        return passingThroughHeads;
    }

    /**
     * Two Snakes will overlap on more than one tile if the heads meet and pass
     * through each other. Or if they both eat each others tail at the same time.
     * This last case may be okay if headToTail is enabled.
     *
     * @param tiles
     * @return List of playerIds of Snakes that overlap
     */
    private Set<String> getOverlappingSnakes(TileMultipleContent[] tiles) {
        final Set<String> overlappingSnakes = new HashSet<>();

        // Find all tiles containing more than one snake part
        final List<List<String>> list = Arrays.stream(tiles)
                .filter(tile -> tile.countInstancesOf(SnakeBody.class) + tile.countInstancesOf(SnakeHead.class) > 1)
                .map(mtile -> mtile.listSnakeIdsPresent()).collect(Collectors.toList());

        // If there exists two or more tiles with the same combination
        // of snake parts they are overlapping
        for (final List<String> snakeIds : list) {
            if (isSnakeIdsOccupyingSameTileMoreThanOnce(snakeIds, tiles)) {
                overlappingSnakes.addAll(snakeIds);
            }
        }

        return overlappingSnakes;
    }

    private void handleTailNibbling(GameFeatures gameFeatures, Map<String, WorldState> snakeToWorldState,
                                    TileMultipleContent tileMultipleContent) {
        try {
            final SnakeBody snakeBodyTail = tileMultipleContent.listContentsOfType(SnakeBody.class).get(0);
            final SnakeHead head = tileMultipleContent.listContentsOfType(SnakeHead.class).get(0);

            // Need to remove tail from original world state!
            WorldState tailWorldState = snakeToWorldState.get(snakeBodyTail.getPlayerId());

            final TailNibbled tailNibbled = new TailNibbled(snakeBodyTail.getPlayerId(), snakeBodyTail.getPosition(),
                    gameFeatures.getNoofRoundsTailProtectedAfterNibble());

            tailWorldState = tailNibbled.transform(tailWorldState);
            snakeToWorldState.put(snakeBodyTail.getPlayerId(), tailWorldState);

            // Assign points
            playerManager.getPlayer(head.getPlayerId()).addPoints(PointReason.NIBBLE,
                    gameFeatures.getPointsPerNibble());
        } catch (final TransformationException e) {
            log.error("TailNibbled transformation failed.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isNibbleSituation(TileMultipleContent tile, Map<String, WorldState> snakeToWorldState) {
        if (tile.size() == 2 && // Nibble is only possible if there exists two objects on the same tile
                tile.listSnakeIdsPresent().size() > 1 && // Not ok to eat your own tail!
                tile.containsExactlyOneOfEachType(new Class[]{SnakeHead.class, SnakeBody.class}) && // One body and
                // one head
                tile.listContentsOfType(SnakeBody.class).get(0).isTail()) { // And the body is a tail

            final String tailPlayerId = tile.listContentsOfType(SnakeBody.class).get(0).getPlayerId();
            final WorldState ws = snakeToWorldState.get(tailPlayerId);

            // Nibble is ok if this player is not currently protected
            return ws.getSnakeHeadById(tailPlayerId).getTailProtectedForGameTicks() == 0;
        }

        return false;
    }

    private boolean isSnakeIdsOccupyingSameTileMoreThanOnce(List<String> playerIds, TileMultipleContent[] tiles) {

        int count = 0;
        for (final TileMultipleContent tile : tiles) {
            final List<String> currentPlayerIds = tile.listSnakeIdsPresent();

            final boolean containsHeadAndTail = tile.containsExactlyOneHeadAndOneTail();
            final boolean headToTailConsumes = gameFeatures.isHeadToTailConsumes();

            if (containsHeadAndTail && headToTailConsumes) {

            } else if (CollectionUtils.isSubCollection(playerIds, currentPlayerIds)) {
                count++;
            }
        }

        return count > 1;
    }

    private TileMultipleContent[] mergeStates(List<WorldState> worldStates) {
        if (worldStates.isEmpty()) {
            return new TileMultipleContent[0];
        }

        final WorldState firstState = worldStates.get(0);
        final TileMultipleContent[] mTiles = convertToTileMultipleContent(firstState.getTiles());

        for (int i = 1; i < worldStates.size(); i++) {
            mergeTiles(mTiles, worldStates.get(i).getTiles());
        }

        return mTiles;
    }

    private TileMultipleContent[] mergeTiles(TileMultipleContent[] first, Tile[] second) {
        IntStream.range(0, first.length).forEach(pos -> {
            final WorldObject contentSecond = second[pos].getContent();
            first[pos].addContent(contentSecond);
        });
        return first;
    }

    private void notifySnakeDied(SnakeHead head, DeathReason deathReason, Coordinate coordinate, long worldTick) {

        snakesDiedThisRound++;

        final IPlayer deadPlayer = playerManager.getPlayer(head.getPlayerId());
        log.info("Death occurred by: {}. GameId: {}, Player: {}, with id: {}, died at: {}", deathReason, gameId,
                deadPlayer.getName(), deadPlayer.getPlayerId(), coordinate);

        deadPlayer.dead(worldTick);

        final SnakeDeadEvent snakeDeadEvent = GameMessageConverter.onPlayerDied(deathReason, head.getPlayerId(),
                coordinate.getX(), coordinate.getY(), gameId, worldTick);

        playerManager.toSet().stream().forEach(player -> {
            player.onSnakeDead(snakeDeadEvent);
        });

        final InternalGameEvent gevent = new InternalGameEvent(System.currentTimeMillis(), snakeDeadEvent);
        globalEventBus.post(gevent);
    }

    private SnakeHead removeSnake(Map<String, WorldState> snakeToWorldState, String snakeId) {
        final WorldState ws = snakeToWorldState.get(snakeId);
        snakeToWorldState.remove(snakeId);
        return ws.getSnakeHeadById(snakeId);
    }

    private TileMultipleContent[] snakeDied(Map<String, WorldState> snakeToWorldState, Collection<String> snakeIds,
                                            DeathReason deathReason, long worldTick) {

        snakeIds.stream().forEach(snakeId -> {
            final SnakeHead snakeHead = removeSnake(snakeToWorldState, snakeId);
            notifySnakeDied(snakeHead, deathReason, startingWorldState.get().translatePosition(snakeHead.getPosition()),
                    worldTick);
        });

        return mergeStates(snakeToWorldState.values().stream().collect(Collectors.toList()));
    }

    private void syncPoints(WorldState ws) {
        playerManager.toSet().stream().forEach(player -> {
            if (player.isAlive()) {
                ws.getSnakeHeadById(player.getPlayerId()).setPoints(player.getTotalPoints());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public WorldState transform(Map<String, Direction> directions, GameFeatures gameFeatures, WorldState ws,
                                boolean spontaneousGrowth, long worldTick) throws TransformationException {

        startingWorldState.set(ws);
        snakesDiedThisRound = 0;
        final int snakesAliveAtStart = ws.listPositionsWithContentOf(SnakeHead.class).length;

        // Get possible world states
        final List<WorldState> worldStates = getAllPossibleWorldStates(ws, directions, spontaneousGrowth, worldTick);

        // Snakes that have collided with a wall or obstacle will
        // already have now been removed (and interested parties notified).

        // All Snakes died, return the world with static objects
        if (worldStates.size() == 0) {
            final KeepOnlyObjectsOfType worldBaseLine = new KeepOnlyObjectsOfType(
                    new Class[]{Empty.class, Food.class, Obstacle.class});
            return worldBaseLine.transform(ws);
        }

        // Find any outstanding illegal states
        // This could be:
        // - SnakeParts occupying the same tile
        // - Two or more SnakeHeads occupying the same tile
        // Remove all offending snake state worlds
        final List<WorldState> validWorldStates = analyzeWorldStates(worldStates, gameFeatures, worldTick);

        // All Snakes died, return the world with static objects
        if (validWorldStates.size() == 0) {
            final KeepOnlyObjectsOfType worldBaseLine = new KeepOnlyObjectsOfType(
                    new Class[]{Empty.class, Food.class, Obstacle.class});
            return worldBaseLine.transform(ws);
        }

        // Create a merged view of all states
        final TileMultipleContent[] mergedTileContent = mergeStates(validWorldStates);

        final Tile[] resultingTiles = createValidWorldState(mergedTileContent);
        final WorldState resultingWorld = new WorldState(ws.getWidth(), ws.getHeight(), resultingTiles);

        syncPoints(resultingWorld);

        final int snakesAliveAtEnd = resultingWorld.listPositionsWithContentOf(SnakeHead.class).length;

        if (snakesAliveAtStart != snakesAliveAtEnd + snakesDiedThisRound) {
            log.error("SnakeHead count doesn't match up. Start: {}, End: {}, Died: " + snakesDiedThisRound,
                    snakesAliveAtStart, snakesAliveAtEnd);
        }
        return resultingWorld;
    }
}
