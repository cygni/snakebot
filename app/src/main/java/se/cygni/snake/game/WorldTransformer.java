package se.cygni.snake.game;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.game.Coordinate;
import se.cygni.game.Tile;
import se.cygni.game.TileMultipleContent;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.exception.ObstacleCollision;
import se.cygni.game.exception.SnakeCollision;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.exception.WallCollision;
import se.cygni.game.transformation.*;
import se.cygni.game.worldobject.*;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.model.DeathReason;
import se.cygni.snake.api.model.PointReason;
import se.cygni.snake.apiconversion.GameMessageConverter;
import se.cygni.snake.event.InternalGameEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldTransformer {

    private static Logger log = LoggerFactory
            .getLogger(WorldTransformer.class);

    private final Game game;
    private int snakesDiedThisRound = 0;

    public WorldTransformer(Game game) {
        this.game = game;
    }

    public WorldState transform(
            Map<String, Direction> directions,
            GameFeatures gameFeatures,
            WorldState ws,
            boolean spontaneousGrowth,
            long worldTick) throws TransformationException {

        snakesDiedThisRound = 0;
        int snakesAliveAtStart = ws.listPositionsWithContentOf(SnakeHead.class).length;

        // Get possible world states
        List<WorldState> worldStates = getAllPossibleWorldStates(ws,
                directions,
                spontaneousGrowth,
                worldTick);

        // Snakes that have collided with a wall or obstacle will
        // already have now been removed (and interested parties notified).

        // All Snakes died, return the world with static objects
        if (worldStates.size() == 0) {
            KeepOnlyObjectsOfType worldBaseLine = new KeepOnlyObjectsOfType(
                    new Class[] {Empty.class, Food.class, Obstacle.class});
            return worldBaseLine.transform(ws);
        }

        // Find any outstanding illegal states
        // This could be:
        // - SnakeParts occupying the same tile
        // - Two or more SnakeHeads occupying the same tile
        // Remove all offending snake state worlds
        List<WorldState> validWorldStates = analyzeWorldStates(
                worldStates, gameFeatures, worldTick);

        // All Snakes died, return the world with static objects
        if (validWorldStates.size() == 0) {
            KeepOnlyObjectsOfType worldBaseLine = new KeepOnlyObjectsOfType(
                    new Class[] {Empty.class, Food.class, Obstacle.class});
            return worldBaseLine.transform(ws);
        }

        // Create a merged view of all states
        TileMultipleContent[] mergedTileContent = mergeStates(validWorldStates);

        Tile[] resultingTiles = createValidWorldState(mergedTileContent);
        WorldState resultingWorld = new WorldState(ws.getWidth(), ws.getHeight(), resultingTiles);

        syncPoints(resultingWorld);

        int snakesAliveAtEnd = resultingWorld.listPositionsWithContentOf(SnakeHead.class).length;

        if (snakesAliveAtStart != snakesAliveAtEnd + snakesDiedThisRound) {
            log.error("SnakeHead count doesn't match up. Start: {}, End: {}, Died: " + snakesDiedThisRound, snakesAliveAtStart, snakesAliveAtEnd);
        }
        return resultingWorld;
    }

    private void syncPoints(WorldState ws) {
        game.getPlayers().stream().forEach(player -> {
            if (player.isAlive())
                ws.getSnakeHeadById(player.getPlayerId()).setPoints(player.getTotalPoints());
        });
    }

    private List<WorldState> analyzeWorldStates(
            List<WorldState> worldStates,
            GameFeatures gameFeatures,
            long worldTick) {

        // Create a map of which states each snake is responsible for
        Map<String, WorldState> snakeToWorldState = new HashMap<>();
        for (WorldState ws : worldStates) {

            snakeToWorldState.put(
                    ws.listSnakeIds().get(0),
                    ws
            );
        }

        // Create a merged view of all states
        TileMultipleContent[] mergedTileContent = mergeStates(worldStates);

        // Find snakes overlapping on more than one tile,
        // this kills both of them.
        Set<String> overlappingSnakes = getOverlappingSnakes(mergedTileContent);
        for (String snakeId : overlappingSnakes) {

            WorldState ws = snakeToWorldState.get(snakeId);
            snakeToWorldState.remove(snakeId);
            SnakeHead snakeHead = ws.getSnakeHeadById(snakeId);
            snakeDied(
                    snakeHead,
                    DeathReason.CollisionWithSnake,
                    ws.translatePosition(snakeHead.getPosition()),
                    worldTick);
        }

        // If there were overlapping snakes we need to update the list of
        // world states and the merged state.
        if (!overlappingSnakes.isEmpty()) {
            worldStates = snakeToWorldState.values().stream().collect(Collectors.toList());
            if (worldStates.isEmpty()) {
                return worldStates;
            }
            mergedTileContent = mergeStates(worldStates);
        }

        // Validate all tiles
        for (TileMultipleContent tile : mergedTileContent) {
            if (!tile.isValidCombinationOfContents()) {

                // Special case when head hits tail and that feature is enabled
                if (gameFeatures.headToTailConsumes &&
                        tile.size() == 2 &&
                        tile.listSnakeIdsPresent().size() > 1 && // Not ok to eat you own tail!
                        tile.containsExactlyOneOfEachType(new Class[] {SnakeHead.class, SnakeBody.class}) &&
                        tile.listContentsOfType(SnakeBody.class).get(0).isTail()) {

                    SnakeBody snakeBodyTail = tile.listContentsOfType(SnakeBody.class).get(0);
                    SnakeHead head = tile.listContentsOfType(SnakeHead.class).get(0);

                    // Need to remove tail from original world state!
                    WorldState tailWorldState = snakeToWorldState.get(snakeBodyTail.getPlayerId());

                    TailNibbled tailNibbled = new TailNibbled(
                            snakeBodyTail.getPlayerId(),
                            snakeBodyTail.getPosition());
                    tailWorldState = tailNibbled.transform(tailWorldState);

                    snakeToWorldState.put(snakeBodyTail.getPlayerId(), tailWorldState);

                    // Assign points
                    game.getPlayer(head.getPlayerId()).addPoints(
                            PointReason.NIBBLE,
                            game.getGameFeatures().getPointsPerNibble()
                    );

                    // Protect nibbled player
                    // Todo: Tailprotection not done.
                    tailWorldState.getSnakeHeadById(snakeBodyTail.getPlayerId());
                    continue;
                }

                // Remove all WorldStates with SnakesHeads involving this clash
                List<SnakeHead> snakeHeads = tile.listOffendingSnakeHeads();
                snakeHeads.stream().forEach(snakeHead -> {
                    WorldState ws = snakeToWorldState.get(snakeHead.getPlayerId());
                    snakeToWorldState.remove(snakeHead.getPlayerId());
                    snakeDied(
                            snakeHead,
                            DeathReason.CollisionWithSnake,
                            ws.translatePosition(snakeHead.getPosition()),
                            worldTick);
                });

                // Assign points to SnakeParts still living
                List<SnakeBody> survivors = tile.listContentsOfType(SnakeBody.class);
                survivors.stream().forEach(snakeBody -> {
                    game.getPlayer(snakeBody.getPlayerId()).addPoints(
                            PointReason.CAUSED_SNAKE_DEATH,
                            game.getGameFeatures().getPointsPerCausedDeath()
                    );
                });
            }
        }

        return snakeToWorldState.values().stream().collect(Collectors.toList());
    }

    /**
     * Two Snakes may overlap on more than one tile if the
     * heads meet and pass through each other.
     *
     * @param tiles
     * @return List of playerIds of Snakes that overlap
     */
    private Set<String> getOverlappingSnakes(TileMultipleContent[] tiles) {
        Set<String> overlappingSnakes = new HashSet<>();

        // Find all tiles containing more than one snake part
        List<List<String>> list = Arrays.stream(tiles).filter(tile ->
            tile.countInstancesOf(SnakeBody.class) +
            tile.countInstancesOf(SnakeHead.class)
                    > 1
        ).map(mtile -> {
            return mtile.listSnakeIdsPresent();
        }).collect(Collectors.toList());

        // If there exists two or more tiles with the same combination
        // of snake parts they are overlapping
        for (List<String> snakeIds : list) {
            if (isSnakeIdsOccupyingSameTileMoreThanOnce(snakeIds, tiles)) {
                overlappingSnakes.addAll(snakeIds);
            }
        }

        return overlappingSnakes;
    }

    private boolean isSnakeIdsOccupyingSameTileMoreThanOnce(
            List<String> playerIds,
            TileMultipleContent[] tiles) {

        int count = 0;
        for (TileMultipleContent tile : tiles) {
            List<String> currentPlayerIds = tile.listSnakeIdsPresent();
            if (CollectionUtils.isSubCollection(playerIds, currentPlayerIds)) {
                count++;
            }
        }

        return count > 1;
    }

    // Get all possible world states (one per snake + static objects)
    private List<WorldState> getAllPossibleWorldStates(
            WorldState ws,
            Map<String, Direction> directions,
            boolean spontaneousGrowth,
            long worldTick) throws TransformationException {

        List<WorldState> worldStates = new ArrayList<>();

        // One world for each Snake
        int[] headPositions = ws.listPositionsWithContentOf(SnakeHead.class);
        for (int headPosition : headPositions) {

            SnakeHead snakeHead = ((SnakeHead)ws.getTile(headPosition).getContent());
            String playerId = snakeHead.getPlayerId();

            // First remove all other objects (just keep the current snake)
            KeepOnlySnakeWithId keepSnake = new KeepOnlySnakeWithId(
                    playerId
            );
            WorldState singleSnakeState = keepSnake.transform(ws);

            // Transform the snake in the choosen direction
            Direction snakeDirection = directions.get(playerId);
            MoveSnake moveSnake = new MoveSnake(
                    singleSnakeState.getSnakeHeadForBodyAt(headPosition),
                    snakeDirection,
                    spontaneousGrowth);

            try {
                WorldState nws = moveSnake.transform(singleSnakeState);

                // Assign points
                if (moveSnake.isFoodConsumed()) {
                    game.getPlayer(playerId).addPoints(
                            PointReason.FOOD,
                            game.getGameFeatures().getPointsPerFood());
                }

                if (moveSnake.isGrowthExecuted()) {
                    game.getPlayer(playerId).addPoints(
                            PointReason.GROWTH,
                            game.getGameFeatures().getPointsPerLength());
                }

                worldStates.add(nws);

            } catch (ObstacleCollision oc) {
                snakeDied(snakeHead,
                        DeathReason.CollisionWithObstacle,
                        ws.translatePosition(oc.getPosition()),
                        worldTick);
            } catch (WallCollision wc) {
                snakeDied(snakeHead,
                        DeathReason.CollisionWithWall,
                        ws.translatePosition(wc.getPosition()),
                        worldTick);
            } catch (SnakeCollision sc) {
                snakeDied(snakeHead,
                        DeathReason.CollisionWithSelf,
                        ws.translatePosition(sc.getPosition()),
                        worldTick);
            } catch (TransformationException oc) {
                snakeDied(snakeHead,
                        DeathReason.CollisionWithObstacle,
                        ws.translatePosition(0),
                        worldTick);
            }
        }

        return worldStates;
    }

    private TileMultipleContent[] mergeStates(List<WorldState> worldStates) {
        WorldState firstState = worldStates.get(0);
        TileMultipleContent[] mTiles = convertToTileMultipleContent(
                firstState.getTiles());

        for (int i = 1; i < worldStates.size(); i++) {
            mergeTiles(mTiles, worldStates.get(i).getTiles());
        }

        return mTiles;
    }

    private TileMultipleContent[] convertToTileMultipleContent(Tile[] tiles) {
        TileMultipleContent[] mTiles = new TileMultipleContent[tiles.length];
        IntStream.range(0, tiles.length).forEach(
                pos -> {
                    WorldObject wo = tiles[pos].getContent();
                    mTiles[pos] = new TileMultipleContent(wo);
                }
        );
        return mTiles;
    }

    private TileMultipleContent[] mergeTiles(TileMultipleContent[] first, Tile[] second) {
        IntStream.range(0, first.length).forEach(
                pos -> {
                    WorldObject contentSecond = second[pos].getContent();
                    first[pos].addContent(contentSecond);
                }
        );
        return first;
    }

    private Tile[] createValidWorldState(TileMultipleContent[] mTiles) {
        Tile[] tiles = new Tile[mTiles.length];

        IntStream.range(0, mTiles.length).forEach(
                pos -> {
                    if (!mTiles[pos].isValidCombinationOfContents()) {
                        throw new IllegalStateException("Tried to create valid world state from invalid merge of states");
                    }

                    tiles[pos] = new Tile(mTiles[pos].getContent());
                }
        );
        return tiles;
    }

    private void snakeDied(
            SnakeHead head,
            DeathReason deathReason,
            Coordinate coordinate,
            long worldTick) {

        snakesDiedThisRound++;
        log.info(head.getPlayerId() + " died at: " + coordinate);
        game.getPlayer(head.getPlayerId()).dead();

        SnakeDeadEvent snakeDeadEvent = GameMessageConverter.onPlayerDied(
                deathReason,
                head.getPlayerId(),
                coordinate.getX(), coordinate.getY(),
                game.getGameId(), worldTick);

        game.getPlayers().stream().forEach( player -> {
            player.onSnakeDead(snakeDeadEvent);
        });

        InternalGameEvent gevent = new InternalGameEvent(
                System.currentTimeMillis(),
                snakeDeadEvent);
        game.getGlobalEventBus().post(gevent);
    }
}
