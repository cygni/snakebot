package se.cygni.snake.client;

import se.cygni.snake.api.model.*;

import java.util.OptionalInt;
import java.util.stream.IntStream;

public class MapUtil {

    private final Map map;
    private final String playerId;
    private final TileContent[] mapFlat;

    public MapUtil(Map map, String playerId) {
        this.map = map;
        this.playerId = playerId;
        mapFlat = flattenMap();
    }

    public boolean canIMoveInDirection(SnakeDirection direction) {
        try {
            MapCoordinate myPos = getMyPosition();
            MapCoordinate myNewPos = myPos.translateBy(0,0);

            switch (direction) {
                case DOWN : myNewPos = myPos.translateBy(0, 1); break;
                case UP : myNewPos = myPos.translateBy(0, -1); break;
                case LEFT : myNewPos = myPos.translateBy(-1, 0); break;
                case RIGHT : myNewPos = myPos.translateBy(1, 0);
            }

            return isTileAvailableForMovementTo(myNewPos);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns an array of MapCoordinate for the snake with the
     * supplied playerId.
     *
     * The first MapCoordinate always points to the MapSnakeHead and
     * the last to the snakes MapSnakeBody tail part.
     *
     * @param playerId
     * @return an array of MapCoordinate for the snake with matching playerId
     */
    public MapCoordinate[] getSnakeSpread(String playerId) {
        return IntStream.range(0, mapFlat.length)
                .filter(pos -> contentAtPosHasPlayerId(pos, playerId))
                .mapToObj(pos -> translatePosition(pos))
                .sorted((coordinate1, coordinate2) -> {

                    // a negative integer, zero, or a positive integer as the first object
                    // is less than, equal to, or greater than the second object.
                    TileContent c1 = getTileAt(coordinate1);
                    TileContent c2 = getTileAt(coordinate2);
                    if (c1 instanceof MapSnakeHead) {
                        return -1;
                    }

                    if (c2 instanceof MapSnakeHead) {
                        return 1;
                    }

                    // We have two SnakeBodies
                    MapSnakeBody body1 = (MapSnakeBody)c1;
                    MapSnakeBody body2 = (MapSnakeBody)c2;

                    return body1.getOrder() < body2.getOrder() ? -1 : 1;
                })
                .toArray(MapCoordinate[]::new);
    }

    private boolean contentAtPosHasPlayerId(int position, String playerId) {
        TileContent content = getTileAt(position);

        if (content instanceof MapSnakeHead) {
            MapSnakeHead head = (MapSnakeHead)content;
            return head.getPlayerId().equals(playerId);
        }

        if (content instanceof MapSnakeBody) {
            MapSnakeBody body = (MapSnakeBody)content;
            return body.getPlayerId().equals(playerId);
        }

        return false;
    }

    /**
     *
     * @return An array containing all MapCoordinates where there's Food
     */
    public MapCoordinate[] listCoordinatesContainingFood() {
        return listCoordinatesContainingType(MapFood.class);
    }

    /**
     *
     * @return An array containing all MapCoordinates where there's an Obstacle
     */
    public MapCoordinate[] listCoordinatesContainingObstacle() {
        return listCoordinatesContainingType(MapObstacle.class);
    }

    private <T extends TileContent> MapCoordinate[]
        listCoordinatesContainingType(Class<T> type) {

        return IntStream.range(0, mapFlat.length)
                .filter(pos -> getTileAt(pos).getClass().equals(type))
                .mapToObj(pos -> translatePosition(pos))
                .toArray(MapCoordinate[]::new);
    }

    /**
     *
     * @param coordinate
     * @return true if the TileContent at coordinate is Empty of contains Food
     */
    public boolean isTileAvailableForMovementTo(MapCoordinate coordinate) {
        try {
            TileContent content = getTileAt(coordinate);
            return (content instanceof MapEmpty) || (content instanceof MapFood);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @return The MapCoordinate of the position for your MapSnakeHead
     */
    public MapCoordinate getMyPosition() {

        OptionalInt optionalPosition = IntStream.range(0, mapFlat.length)
                .filter(pos -> getTileAt(pos) instanceof MapSnakeHead)
                .filter(snakeHeadPos -> {
                    MapSnakeHead head = (MapSnakeHead)getTileAt(snakeHeadPos);
                    return head.getPlayerId().equals(playerId);
                })
                .findFirst();

        if (optionalPosition.isPresent()) {
            return translatePosition(optionalPosition.getAsInt());
        }

        throw new IllegalStateException("Could not find my position");
    }

    /**
     * Represents the Map as a single array. Use the translatePosition(...) and
     * translateCoordinate(...) to convert between the different systems.
     *
     * @return A single array containing all the Tiles of the Map.
     */
    public TileContent[] flattenMap() {

        return IntStream.range(0, map.getWidth() * map.getHeight())
                .mapToObj(pos -> getTileAt(translatePosition(pos)))
                .toArray(TileContent[]::new);
    }

    /**
     *
     * @param position
     * @return the TileContent at the specified position of the flattened map.
     */
    public TileContent getTileAt(int position) {
        return mapFlat[position];
    }

    /**
     *
     * @param coordinate
     * @return the TileContent at the specified coordinate
     */
    public TileContent getTileAt(MapCoordinate coordinate) {
        return map.getTiles()[coordinate.x][coordinate.y];
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
    public int translateCoordinate(MapCoordinate coordinate) {
        return coordinate.x + coordinate.y * map.getWidth();
    }
}
