package se.cygni.snake.client;

import org.junit.Test;
import se.cygni.snake.api.model.*;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class MapUtilTest {
    /*
        0 1 2
        3 4 5
        6 7 8
    */
    @Test
    public void testCanIMoveInDirectionNearEdge() throws Exception {
        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {5, 8, 7}, 0),
        };
        Map map = createMap(snakeInfos, new int[6], new int[3]);

        MapUtil mapUtil = new MapUtil(map, "a");

        assertTrue(mapUtil.canIMoveInDirection(SnakeDirection.UP));
        assertTrue(mapUtil.canIMoveInDirection(SnakeDirection.LEFT));
        assertFalse(mapUtil.canIMoveInDirection(SnakeDirection.DOWN));
        assertFalse(mapUtil.canIMoveInDirection(SnakeDirection.RIGHT));
    }

    @Test
    public void testCanIMoveInDirection() throws Exception {
        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {7, 4, 5, 8}, 0),
        };
        Map map = createMap(snakeInfos, new int[6], new int[3]);

        MapUtil mapUtil = new MapUtil(map, "a");

        assertFalse(mapUtil.canIMoveInDirection(SnakeDirection.UP));
        assertTrue(mapUtil.canIMoveInDirection(SnakeDirection.LEFT));
        assertFalse(mapUtil.canIMoveInDirection(SnakeDirection.DOWN));
        assertFalse(mapUtil.canIMoveInDirection(SnakeDirection.RIGHT));
    }

    @Test
    public void testIsTileAvailableForMovementTo() throws Exception {
        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {7, 4, 5, 8}, 0),
        };
        Map map = createMap(snakeInfos, new int[0], new int[]{0});

        MapUtil mapUtil = new MapUtil(map, "a");

        assertFalse(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(1,2)));
        assertFalse(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(1,1)));
        assertFalse(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(2,1)));
        assertFalse(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(2,2)));
        assertFalse(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(-1,-1)));

        // Obstacle at 0,0
        assertFalse(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(0,0)));

        assertTrue(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(0,1)));
        assertTrue(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(0,2)));
        assertTrue(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(1,0)));
        assertTrue(mapUtil.isTileAvailableForMovementTo(new MapCoordinate(2,0)));
    }

    @Test
    public void testGetSnakeSpread() throws Exception {

        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {8, 7, 4, 1}, 0),
                new SnakeInfo("b", 8, "b", new int[] {3, 0}, 0)
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

    @Test(expected = RuntimeException.class)
    public void testTranslateCoordinateWithNegativeX() {
        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {8, 7, 4, 1}, 0),
                new SnakeInfo("b", 8, "b", new int[] {3, 0}, 0)
        };

        Map map = createMap(snakeInfos, new int[]{}, new int[]{});

        MapUtil mapUtil = new MapUtil(map, "a");

        TileContent tileAt = mapUtil.getTileAt(new MapCoordinate(-1, 0));
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

    @Test
    public void testTranslateCoordinates() throws Exception {
        MapUtil mapUtil = new MapUtil(createMap(
                new SnakeInfo[0], new int[0], new int[0]
        ), "a");

        MapCoordinate[] coordinates = new MapCoordinate[] {
                new MapCoordinate(0,0),
                new MapCoordinate(1,1),
                new MapCoordinate(2,2) };

        assertArrayEquals(new int[] {0,4,8},
                mapUtil.translateCoordinates(coordinates));
    }

    @Test
    public void testTranslateCoordinateLarge() throws Exception {
        Map map = new Map(50, 50, 8, new SnakeInfo[0], new int[0], new int[0]);
        MapUtil mapUtil = new MapUtil(map, "a");

        System.out.println(mapUtil.translatePosition(637));
        System.out.println(mapUtil.translatePosition(687));
    }
    @Test
    public void testGetPlayerLength() throws Exception {

        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {8, 7, 4, 1}, 0),
                new SnakeInfo("b", 8, "b", new int[] {3, 0}, 0)
        };

        Map map = createMap(snakeInfos, new int[]{}, new int[]{});
        MapUtil mapUtil = new MapUtil(map, "a");

        assertEquals(4, mapUtil.getPlayerLength("a"));
        assertEquals(2, mapUtil.getPlayerLength("b"));
    }

    @Test
    public void testGetMyPosition() {
        SnakeInfo[] snakeInfos = new SnakeInfo[] {
                new SnakeInfo("a", 3, "a", new int[] {8, 7, 4, 1}, 0),
                new SnakeInfo("b", 8, "b", new int[] {3, 0}, 0)
        };

        MapCoordinate snakePosition = new MapCoordinate(2,2);

        Map map = createMap(snakeInfos, new int[]{}, new int[]{});
        MapUtil mapUtil = new MapUtil(map, "a");
        assertEquals(snakePosition, mapUtil.getMyPosition());
    }

    /*
        0 1 2
        3 4 5
        6 7 8
    */
    private Map createMap(SnakeInfo[] snakeInfos, int[] foods, int[] obstacles) {
        Map map = new Map(3, 3, 8, snakeInfos, foods, obstacles);
        return map;
    }


}