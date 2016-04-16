package se.cygni.snake.client;

import org.apache.commons.lang3.ArrayUtils;
import se.cygni.snake.api.model.*;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

public class MapUtil {

    private final Map map;
    private final int mapSize;
    private final String playerId;
    private final java.util.Map<String, SnakeInfo> snakeInfoMap;
    private final java.util.Map<String, BitSet> snakeSpread;
    private final BitSet foods;
    private final BitSet obstacles;
    private final BitSet snakes;


    public MapUtil(Map map, String playerId) {
        this.map = map;
        this.mapSize = map.getHeight() * map.getWidth();

        this.playerId = playerId;
        snakeInfoMap = new HashMap<>();
        snakeSpread = new HashMap<>();

        int mapLength = map.getHeight() * map.getWidth();
        foods = new BitSet(mapLength);
        obstacles = new BitSet(mapLength);
        snakes = new BitSet(mapLength);

        populateSnakeInfo();
        populateStaticTileBits();
    }

    public boolean canIMoveInDirection(SnakeDirection direction) {
        try {
            MapCoordinate myPos = getMyPosition();
            MapCoordinate myNewPos = myPos.translateBy(0, 0);

            switch (direction) {
                case DOWN:
                    myNewPos = myPos.translateBy(0, 1);
                    break;
                case UP:
                    myNewPos = myPos.translateBy(0, -1);
                    break;
                case LEFT:
                    myNewPos = myPos.translateBy(-1, 0);
                    break;
                case RIGHT:
                    myNewPos = myPos.translateBy(1, 0);
            }

            return isTileAvailableForMovementTo(myNewPos);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns an array of MapCoordinate for the snake with the
     * supplied playerId.
     * <p>
     * The first MapCoordinate always points to the MapSnakeHead and
     * the last to the snakes MapSnakeBody tail part.
     *
     * @param playerId
     * @return an array of MapCoordinate for the snake with matching playerId
     */
    public MapCoordinate[] getSnakeSpread(String playerId) {
        return translatePositions(
                snakeInfoMap.get(playerId).getPositions());
    }

    public int getPlayerLength(String playerId) {
        return snakeInfoMap.get(playerId)
                .getLength();
    }

    /**
     * @return An array containing all MapCoordinates where there's Food
     */
    public MapCoordinate[] listCoordinatesContainingFood() {
        return translatePositions(map.getFoodPositions());
    }

    /**
     * @return An array containing all MapCoordinates where there's an Obstacle
     */
    public MapCoordinate[] listCoordinatesContainingObstacle() {
        return translatePositions(map.getObstaclePositions());
    }

    /**
     * @param coordinate
     * @return true if the TileContent at coordinate is Empty or contains Food
     */
    public boolean isTileAvailableForMovementTo(MapCoordinate coordinate) {
        if (isCoordinateOutOfBounds(coordinate))
            return false;

        int position = translateCoordinate(coordinate);
        return isTileAvailableForMovementTo(position);
    }

    /**
     * @param position map position
     * @return true if the TileContent at map position is Empty or contains Food
     */
    private boolean isTileAvailableForMovementTo(int position) {
        if (isPositionOutOfBounds(position))
            return false;

        return !(obstacles.get(position) || snakes.get(position));
    }

    /**
     * @return The MapCoordinate of your snake's head.
     */
    public MapCoordinate getMyPosition() {
        return translatePosition(
                snakeInfoMap.get(playerId).getPositions()[0]);
    }

    /**
     * @param coordinate map coordinate
     * @return whether or not it is out of bounds
     */
    public boolean isCoordinateOutOfBounds(MapCoordinate coordinate) {
        return coordinate.x < 0 || coordinate.x >= map.getWidth() || coordinate.y < 0 || coordinate.y >= map.getHeight();
    }

    /**
     * @param position map position
     * @return whether or not it is out of bounds
     */
    private boolean isPositionOutOfBounds(int position) {
        return position < 0 || position >= mapSize;
    }

    /**
     * @param position map position
     * @return the TileContent at the specified position of the flattened map.
     */
    private TileContent getTileAt(int position) {
        if (isPositionOutOfBounds(position)) {
            String errorMessage = String.format("Position [%s] is out of bounds", position);
            throw new RuntimeException(errorMessage);
        }

        if (foods.get(position)) {
            return new MapFood();
        }

        if (obstacles.get(position)) {
            return new MapObstacle();
        }

        if (snakes.get(position)) {
            return getSnakePart(position);
        }

        return new MapEmpty();
    }

    /**
     * @param coordinate
     * @return the TileContent at the specified coordinate
     */
    public TileContent getTileAt(MapCoordinate coordinate) {
        return getTileAt(translateCoordinate(coordinate));
    }

    /**
     * Converts a position in the flattened single array representation
     * of the Map to a MapCoordinate.
     *
     * @param position
     * @return
     */
    public MapCoordinate translatePosition(int position) {
        int y = position / map.getWidth();
        int x = position - y * map.getWidth();
        return new MapCoordinate(x, y);
    }

    /**
     * Converts a MapCoordinate to the same position in the flattened
     * single array representation of the Map.
     *
     * @param coordinate
     * @return
     */
    private int translateCoordinate(MapCoordinate coordinate) {
        if (isCoordinateOutOfBounds(coordinate)) {
            String errorMessage = String.format("Coordinate [%s,%s] is out of bounds", coordinate.x, coordinate.y);
            throw new RuntimeException(errorMessage);
        }

        return coordinate.x + coordinate.y * map.getWidth();
    }

    private MapCoordinate[] translatePositions(int[] positions) {
        return Arrays.stream(positions)
                .mapToObj(pos -> translatePosition(pos))
                .toArray(MapCoordinate[]::new);
    }

    public int[] translateCoordinates(MapCoordinate[] coordinates) {
        return Arrays.stream(coordinates)
                .mapToInt(coordinate -> translateCoordinate(coordinate))
                .toArray();
    }

    private TileContent getSnakePart(int position) {
        String playerId = getPlayerIdAtPosition(position);

        SnakeInfo snakeInfo = snakeInfoMap.get(playerId);

        int order = ArrayUtils.indexOf(snakeInfo.getPositions(), position);

        if (order == 0) {
            return new MapSnakeHead(snakeInfo.getName(), playerId);
        }

        if (order == snakeInfo.getLength() - 1) {
            return new MapSnakeBody(true, playerId, order);
        }
        return new MapSnakeBody(false, playerId, order);
    }

    private String getPlayerIdAtPosition(int position) {
        for (SnakeInfo snakeInfo : map.getSnakeInfos()) {
            if (snakeSpread
                    .get(snakeInfo.getId())
                    .get(position)) {

                return snakeInfo.getId();
            }
        }
        throw new RuntimeException("No snake at position: " + position);
    }

    private void populateSnakeInfo() {
        for (SnakeInfo snakeInfo : map.getSnakeInfos()) {
            snakeInfoMap.put(snakeInfo.getId(), snakeInfo);

            BitSet snakePositions = new BitSet(map.getHeight() * map.getWidth());
            for (int pos : snakeInfo.getPositions()) {
                snakes.set(pos);
                snakePositions.set(pos);
            }

            snakeSpread.put(snakeInfo.getId(), snakePositions);
        }
    }

    private void populateStaticTileBits() {
        for (int pos : map.getFoodPositions()) {
            foods.set(pos);
        }
        for (int pos : map.getObstaclePositions()) {
            obstacles.set(pos);
        }
    }
}
