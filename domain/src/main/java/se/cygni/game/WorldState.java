package se.cygni.game;

import org.apache.commons.lang3.ArrayUtils;
import se.cygni.game.enums.Direction;
import se.cygni.game.exception.OutOfBoundsException;
import se.cygni.game.worldobject.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldState {

    private final int width, height;
    private final Tile[] tiles;

    public WorldState(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = createEmptyTiles();
    }

    public WorldState(int width, int height, Tile[] tiles) {
        this.width = width;
        this.height = height;
        this.tiles = ArrayUtils.clone(tiles);
    }

    public WorldState(WorldState worldState) {
        this.width = worldState.getWidth();
        this.height = worldState.getHeight();
        this.tiles = worldState.getTiles();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Coordinate translatePosition(int position) {
        int y = position / width;
        int x = position - y * width;
        return new Coordinate(x, y);
    }

    public int translateCoordinate(Coordinate coordinate) {
        return coordinate.getX() + coordinate.getY() * width;
    }

    public int getSize() {
        return width * height;
    }

    public Tile getTile(int position) {
        if (position < 0)
            throw new OutOfBoundsException("Can not get tiles at negative position");
        if (position >= getSize())
            throw new OutOfBoundsException("Can not get tiles beyond world");

        return tiles[position];
    }

    public boolean isTileEmpty(int position) {
        return isTileContentOfType(position, Empty.class);
    }

    public <T extends WorldObject> boolean isTileContentOfType(int position, Class<T> clazz) {
        return getTile(position).getContent().getClass() == clazz;
    }

    public SnakeHead getSnakeHeadById(String playerId) {
        int[] snakeHeadPositions = listPositionsWithContentOf(SnakeHead.class);
        for (int pos : snakeHeadPositions) {
            SnakeHead head = (SnakeHead)tiles[pos].getContent();
            if (head.getPlayerId().equals(playerId)) {
                return head;
            }
        }
        throw new IllegalArgumentException("Could not find SnakeHead with playerId " + playerId);
    }

    public SnakeHead getSnakeHeadForBodyAt(int position) {
        if (! (getTile(position).getContent() instanceof SnakePart)) {
            throw new RuntimeException("Tile at position " + position + " didn't contain a SnakePart");
        }

        int[] snakeHeadPositions = listPositionsWithContentOf(SnakeHead.class);
        for (int snakeHeadPosition : snakeHeadPositions) {
            SnakeHead snakeHead = (SnakeHead)getTile(snakeHeadPosition).getContent();
            int[] snakeSpread = getSnakeSpread(snakeHead);
            if (ArrayUtils.contains(snakeSpread, position))
                return snakeHead;
        }

        throw new IllegalStateException("Found SnakePart without head");
    }

    public List<String> listSnakeIds() {

        return Arrays.stream(tiles).filter(tile -> tile.getContent() instanceof SnakeHead)
                .map(tile1 -> {
                    return ((SnakeHead)tile1.getContent()).getPlayerId();
                }).collect(Collectors.toList());
    }

    /**
     *
     * @param snakeHead
     * @return an array of all positions that this snake occupies
     */
    public int[] getSnakeSpread(SnakeHead snakeHead) {
        int[] snakeSpread = new int[snakeHead.getLength()];
        int counter = 0;
        SnakePart snakePart = (SnakePart)snakeHead;
        while (snakePart != null) {
            snakeSpread[counter++] = snakePart.getPosition();
            snakePart = snakePart.getNextSnakePart();
        }
        return snakeSpread;
    }

    public int getPositionForAdjacent(int position, Direction direction) {
        if (!hasAdjacentTile(position, direction))
            throw new OutOfBoundsException("Tile " + direction + " from position " + position + " is out of bounds");

        switch (direction) {
            case DOWN:  return position + width;
            case UP:    return position - width;
            case RIGHT: return position + 1;
            case LEFT:  return position - 1;
            default:    throw new RuntimeException("Invalid direction");
        }
    }

    /**
     * Positions that are adjacent to a SnakeHead are
     * considered illegal. This is because no action
     * on these tiles should be taken since it could
     * cause a snake to collide.
     *
     * @return
     */
    public int[] listPositionsAdjacentToSnakeHeads() {
        int[] snakeHeadPositions = listPositionsWithContentOf(SnakeHead.class);

        return IntStream.of(snakeHeadPositions).flatMap(pos ->
            IntStream.of(
                    listAdjacentTiles(pos)
            )
        ).toArray();
    }

    public int[] listAdjacentTiles(int position) {
        return Stream.of(Direction.values())
                .mapToInt(direction -> {
                    if (hasAdjacentTile(position, direction))
                        return getPositionForAdjacent(position, direction);
                    return -1;
                })
                .filter(p -> p >= 0)
                .toArray();
    }

    /**
     *
     * @param position
     * @param direction
     * @return True if the adjacent is within bounds (i.e. not a wall)
     */
    public boolean hasAdjacentTile(int position, Direction direction) {
        switch (direction) {
            case UP   : return (position-width >= 0);
            case DOWN : return (position+width < getSize());
            case LEFT : return (position % width != 0);
            case RIGHT: return ((position+1) % width != 0);
            default   : return false;
        }
    }

    public <T extends WorldObject> int[] listPositionsWithContentOf(Class<T> clazz) {
        return IntStream.range(0, getSize()).filter( position ->
            isTileContentOfType(position, clazz)).toArray();
    }

    public int[] listEmptyPositionsWithPadding() {
        return IntStream.range(0, getSize()).filter( position ->
            isTileEmpty(position) && !hasAdjacentFilledTile(position)
        ).toArray();
    }

    public boolean hasAdjacentFilledTile(int pos) {
        return IntStream.of(listAdjacentTiles(pos)).filter( position ->
            !isTileEmpty(position)
        ).findFirst().isPresent();
    }

    public int[] listNonEmptyPositions() {
        return IntStream.range(0, getSize()).filter( position ->
                !isTileEmpty(position)).toArray();
    }

    public int[] listEmptyPositions() {
        return listPositionsWithContentOf(Empty.class);
    }

    public int[] listFoodPositions() {
        return listPositionsWithContentOf(Food.class);
    }

    public int[] listObstaclePositions() {
        return listPositionsWithContentOf(Obstacle.class);
    }

    public int[] listEmptyValidPositions() {
        int[] emptyPositions = listEmptyPositions();
        int[] snakeAdjacentPositions = listPositionsAdjacentToSnakeHeads();

        return IntStream.of(emptyPositions).filter(pos ->
            !ArrayUtils.contains(snakeAdjacentPositions, pos)
        ).toArray();
    }

    /**
     * @return a copy of the tiles
     */
    public Tile[] getTiles() {
        return ArrayUtils.clone(tiles);
    }

    /**
     * The world is represented by a single array
     */
    private Tile[] createEmptyTiles() {
        int size = getSize();
        Tile[] tiles = new Tile[size];

        IntStream.range(0, size).forEach(
                pos -> {
                    tiles[pos] = new Tile();
                }
        );
        return tiles;
    }
}
