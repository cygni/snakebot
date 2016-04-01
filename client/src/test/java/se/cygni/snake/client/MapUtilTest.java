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
    public void testCanIMoveInDirection() throws Exception {

    }

/*
0 1 2
3 4 5
6 7 8
*/
    @Test
    public void testIsTileAvailableForMovementTo() throws Exception {
        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {7, 4, 5, 8}),
        };
        Map map = createMap(snakeInfos, new int[0], new int[0]);

        MapUtil mapUtil = new MapUtil(map, "a");

        assertFalse(mapUtil.canIMoveInDirection(SnakeDirection.UP));
        assertTrue(mapUtil.canIMoveInDirection(SnakeDirection.LEFT));
        assertFalse(mapUtil.canIMoveInDirection(SnakeDirection.DOWN));
        assertFalse(mapUtil.canIMoveInDirection(SnakeDirection.RIGHT));
    }

    @Test
    public void testGetSnakeSpread() throws Exception {

        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {8, 7, 4, 1}),
                new SnakeInfo("b", 8, "b", new int[] {3, 0})
        };

        Map map = createMap(snakeInfos, new int[]{}, new int[]{});

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
        MapUtil mapUtil = new MapUtil(createMap(
                new SnakeInfo[0], new int[] {3,0,2}, new int[] {8,1}), "a");
        MapCoordinate[] obstacles = mapUtil.listCoordinatesContainingObstacle();

        assertEquals(2, obstacles.length);

        Stream.of(obstacles).forEach(obstacleCoordinate -> {
            assertTrue(mapUtil.getTileAt(obstacleCoordinate) instanceof MapObstacle);
        });
    }

    @Test
    public void testListCoordinatesContainingFood() throws Exception {
        MapUtil mapUtil = new MapUtil(createMap(
                new SnakeInfo[0], new int[] {3,0,2}, new int[0]), "a");

        MapCoordinate[] foods = mapUtil.listCoordinatesContainingFood();

        assertEquals(3, foods.length);

        Stream.of(foods).forEach(foodCoordinate -> {
            assertTrue(mapUtil.getTileAt(foodCoordinate) instanceof MapFood);
        });
    }

/*
0 1 2
3 4 5
6 7 8
*/
    @Test
    public void testGetMyPosition() throws Exception {
        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {7, 4, 5, 8}),
        };
        Map map = createMap(snakeInfos, new int[0], new int[0]);

        MapUtil mapUtil = new MapUtil(map, "a");

        assertEquals(new MapCoordinate(1,2), mapUtil.getMyPosition());
    }

    @Test
    public void testTranslatePosition() throws Exception {
        MapUtil mapUtil = new MapUtil(createMap(
                new SnakeInfo[0], new int[0], new int[0]
        ), "a");
        MapCoordinate coordinate = mapUtil.translatePosition(5);
        assertEquals(2, coordinate.x);
        assertEquals(1, coordinate.y);
    }

    @Test
    public void testTranslateCoordinate() throws Exception {
        MapUtil mapUtil = new MapUtil(createMap(
                new SnakeInfo[0], new int[0], new int[0]
        ), "a");
        int position = mapUtil.translateCoordinate(new MapCoordinate(0, 2));
        assertEquals(6, position);
    }

    @Test
    public void testGetPlayerLength() throws Exception {

        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {8, 7, 4, 1}),
                new SnakeInfo("b", 8, "b", new int[] {3, 0})
        };

        Map map = createMap(snakeInfos, new int[]{}, new int[]{});
        MapUtil mapUtil = new MapUtil(map, "a");

        assertEquals(4, mapUtil.getPlayerLength("a"));
        assertEquals(2, mapUtil.getPlayerLength("b"));
    }

    private Map createMap(SnakeInfo[] snakeInfos, int[] foods, int[] obstacles) {
        Map map = new Map(3, 3, 8, snakeInfos, foods, obstacles);
        return map;
    }


}