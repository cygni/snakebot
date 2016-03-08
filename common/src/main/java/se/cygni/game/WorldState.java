package se.cygni.game;

import org.apache.commons.lang3.ArrayUtils;
import se.cygni.game.enums.Direction;
import se.cygni.game.exception.OutOfBoundsException;
import se.cygni.game.worldobject.*;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldState {

    private int width, height;
    private Tile[] tiles;

    public WorldState(int width, int height) {
        this.width = width;
        this.height = height;
        initEmptyWorld();
    }

    public WorldState(int width, int height, Tile[] tiles) {
        this.width = width;
        this.height = height;
        this.tiles = ArrayUtils.clone(tiles);
    }

    public WorldState(WorldState worldState) {
        this.width = worldState.getWidth();
        this.height = worldState.getHeight();
        this.tiles = ArrayUtils.clone(worldState.getTiles());
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
    private void initEmptyWorld() {
        int size = getSize();
        tiles = new Tile[size];

        IntStream.range(0, size).forEach(
                pos -> {
                    tiles[pos] = new Tile();
                }
        );
    }
}
