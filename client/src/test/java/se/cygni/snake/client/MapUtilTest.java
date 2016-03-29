package se.cygni.snake.client;

import org.junit.Test;
import se.cygni.snake.api.model.*;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapUtilTest {

    @Test
    public void testIsTileAvailableForMovementTo() throws Exception {

    }

    @Test
    public void testGetSnakeSpread() throws Exception {
        Map map = createMap();

        map.getTiles()[2][2] = new MapSnakeHead("test", "a");
        map.getTiles()[1][2] = new MapSnakeBody(false, "a", 1);
        map.getTiles()[1][1] = new MapSnakeBody(false, "a", 2);
        map.getTiles()[1][0] = new MapSnakeBody(true, "a", 3);

        map.getTiles()[0][1] = new MapSnakeHead("test-other", "b");
        map.getTiles()[0][0] = new MapSnakeBody(false, "b", 1);

        MapUtil mapUtil = new MapUtil(map, "a");

        MapCoordinate[] snakeSpread = mapUtil.getSnakeSpread("a");

        assertEquals(4, snakeSpread.length);

        MapSnakeHead head = (MapSnakeHead) mapUtil.getTileAt(snakeSpread[0]);
        assertEquals("a", head.getPlayerId());

        IntStream.range(1, 4).forEach(pos -> {
            MapSnakeBody body = (MapSnakeBody) mapUtil.getTileAt(snakeSpread[pos]);
            assertEquals("a", body.getPlayerId());

            if (pos < 3) {
                assertFalse(body.isTail());
            } else {
                assertTrue(body.isTail());
            }
        });
    }

    @Test
    public void testListCoordinatesContainingObstacle() throws Exception {
        MapUtil mapUtil = new MapUtil(createMap(), "a");
        MapCoordinate[] obstacles = mapUtil.listCoordinatesContainingObstacle();

        assertEquals(1, obstacles.length);

        Stream.of(obstacles).forEach(obstacleCoordinate -> {
            assertTrue(mapUtil.getTileAt(obstacleCoordinate) instanceof MapObstacle);
        });
    }

    @Test
    public void testListCoordinatesContainingFood() throws Exception {
        MapUtil mapUtil = new MapUtil(createMap(), "a");
        MapCoordinate[] foods = mapUtil.listCoordinatesContainingFood();

        assertEquals(2, foods.length);

        Stream.of(foods).forEach(foodCoordinate -> {
            assertTrue(mapUtil.getTileAt(foodCoordinate) instanceof MapFood);
        });
    }

    @Test
    public void testGetMyPosition() throws Exception {
        Map map = createMap();

        map.getTiles()[1][2] = new MapSnakeHead("test", "a");
        MapUtil mapUtil = new MapUtil(map, "a");

        assertEquals(new MapCoordinate(1,2), mapUtil.getMyPosition());
    }

    @Test
    public void testFlattenMap() throws Exception {
        Map map = createMap();
        MapUtil mapUtil = new MapUtil(map, "a");

        TileContent[][] contents = map.getTiles();
        TileContent[] contentsFlat = mapUtil.flattenMap();

        IntStream.range(0, contentsFlat.length).forEach(pos -> {
            MapCoordinate coordinate = mapUtil.translatePosition(pos);
            TileContent mapContent = contents[coordinate.x][coordinate.y];
            TileContent flatContent = contentsFlat[pos];

            assertTrue(
                    "Tile at " + pos + " not equal to tile at " + coordinate,
                    mapContent == flatContent);
        });
    }

    @Test
    public void testTranslatePosition() throws Exception {
        MapUtil mapUtil = new MapUtil(createMap(), "a");
        MapCoordinate coordinate = mapUtil.translatePosition(5);
        assertEquals(2, coordinate.x);
        assertEquals(1, coordinate.y);
    }

    @Test
    public void testTranslateCoordinate() throws Exception {
        MapUtil mapUtil = new MapUtil(createMap(), "a");
        int position = mapUtil.translateCoordinate(new MapCoordinate(0, 2));
        assertEquals(6, position);
    }

    @Test
    public void testGetPlayerLength() throws Exception {
        Map map = createMap();

        map.getTiles()[2][2] = new MapSnakeHead("test", "a");
        map.getTiles()[1][2] = new MapSnakeBody(false, "a", 1);
        map.getTiles()[1][1] = new MapSnakeBody(false, "a", 2);
        map.getTiles()[1][0] = new MapSnakeBody(true, "a", 3);

        map.getTiles()[0][1] = new MapSnakeHead("test-other", "b");
        map.getTiles()[0][0] = new MapSnakeBody(false, "b", 1);

        MapUtil mapUtil = new MapUtil(map, "a");

        assertEquals(4, mapUtil.getPlayerLength("a"));
        assertEquals(2, mapUtil.getPlayerLength("b"));
    }

    private Map createMap() {
        TileContent[][] contents = new TileContent[][] {
                {new MapEmpty(), new MapEmpty(), new MapEmpty()},
                {new MapFood(), new MapEmpty(), new MapEmpty()},
                {new MapEmpty(), new MapFood(), new MapObstacle()}
        };
        Map map = new Map(3, 3, 0, contents, null);
        return map;
    }
}