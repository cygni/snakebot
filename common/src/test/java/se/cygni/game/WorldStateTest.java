package se.cygni.game;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Ignore;
import org.junit.Test;
import se.cygni.game.enums.Direction;
import se.cygni.game.exception.OutOfBoundsException;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.worldobject.*;

import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class WorldStateTest {

    @Test
    public void testGetSize() throws Exception {
        WorldState ws = new WorldState(10, 10);
        assertEquals(100, ws.getSize());
    }

    @Test
    public void testGetTile() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Tile[] tiles = ws.getTiles();

        // Place a food on tile 12 => coordinate 2,1
        Food food = new Food();
        tiles[12] = new Tile(food);

        ws = new WorldState(10, 10, tiles);

        assertEquals(food, ws.getTile(12).getContent());
    }

    @Test(expected = OutOfBoundsException.class)
    public void testGetTileNegativePosition() throws Exception {
        WorldState ws = new WorldState(10, 10);
        ws.getTile(-1);
    }

    @Test(expected = OutOfBoundsException.class)
    public void testGetTileOutOfBounds() throws Exception {
        WorldState ws = new WorldState(10, 10);
        ws.getTile(100);
    }

    @Test
    public void testGetTileBoundaryCheck() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Food f1 = new Food();
        Obstacle o1 = new Obstacle();

        ws = SnakeTestUtil.replaceWorldObjectAt(ws, f1, 0);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, o1, 99);

        assertThat(ws.getTile(0).getContent(), equalTo(f1));
        assertThat(ws.getTile(99).getContent(), equalTo(o1));
    }

    @Test
    public void testTranslateCoordinate() throws Exception {
        WorldState ws = new WorldState(10, 10);

        assertEquals("Wrong translation to position",
                15, ws.translateCoordinate(new Coordinate(5,1)));

        assertEquals("Wrong translation to position",
                99, ws.translateCoordinate(new Coordinate(9,9)));
    }

    @Test
    public void testTranslatePosition() throws Exception {
        WorldState ws = new WorldState(6, 3);

        Coordinate expectedCoordinate = new Coordinate(2, 1);
        Coordinate coordinate = ws.translatePosition(8);

        assertEquals("Y coordinate wrong", expectedCoordinate.getY(), coordinate.getY());
        assertEquals("X coordinate wrong", expectedCoordinate.getX(), coordinate.getX());
    }

    @Test
    public void testTranslatePositionLast() throws Exception {
        WorldState ws = new WorldState(10, 10);

        Coordinate expectedCoordinate = new Coordinate(9, 9);
        Coordinate coordinate = ws.translatePosition(99);

        assertEquals("Y coordinate wrong", expectedCoordinate.getY(), coordinate.getY());
        assertEquals("X coordinate wrong", expectedCoordinate.getX(), coordinate.getX());
    }

    @Test
    public void testIsTileEmpty() throws Exception {
        // Place a food on tile 12 => coordinate 2,1
        WorldState ws = SnakeTestUtil.createWorld(Food.class, 10, 10, 12);

        assertFalse(ws.isTileEmpty(12));
        assertTrue(ws.isTileEmpty(0));
    }

    @Test
    public void testGetPositionForAdjacent() throws Exception {
        WorldState ws = new WorldState(10, 10);

        // Normal adjacents

        // 0,0 DOWN
        assertEquals(10, ws.getPositionForAdjacent(0, Direction.DOWN));

        // 0,0 RIGHT
        assertEquals(1, ws.getPositionForAdjacent(0, Direction.RIGHT));

        // 10,10 UP
        assertEquals(89, ws.getPositionForAdjacent(99, Direction.UP));

        // 10,10 LEFT
        assertEquals(98, ws.getPositionForAdjacent(99, Direction.LEFT));

        // 0,1 UP
        assertEquals(0, ws.getPositionForAdjacent(10, Direction.UP));

        // Corner adjacents out of bounds

        try {
            // Should fail
            assertEquals(-10, ws.getPositionForAdjacent(0, Direction.UP));
            fail("Allowed adjacent tile out of bounds, UP from 0,0");
        } catch (RuntimeException rte) {}

        try {
            // Should fail
            assertEquals(-1, ws.getPositionForAdjacent(0, Direction.LEFT));
            fail("Allowed adjacent tile out of bounds, LEFT from 0,0");
        } catch (RuntimeException rte) {}

        try {
            // Should fail
            assertEquals(100, ws.getPositionForAdjacent(99, Direction.RIGHT));
            fail("Allowed adjacent tile out of bounds, RIGHT from 9,9");
        } catch (RuntimeException rte) {}

        try {
            // Should fail
            assertEquals(109, ws.getPositionForAdjacent(99, Direction.DOWN));
            fail("Allowed adjacent tile out of bounds, DOWN from 9,9");
        } catch (RuntimeException rte) {}

        // Left and Right edge adjacents out of bounds

        try {
            // Should fail
            assertEquals(20, ws.getPositionForAdjacent(19, Direction.RIGHT));
            fail("Allowed adjacent tile out of bounds, RIGHT from 1,9");
        } catch (RuntimeException rte) {}

        try {
            // Should fail
            assertEquals(9, ws.getPositionForAdjacent(10, Direction.LEFT));
            fail("Allowed adjacent tile out of bounds, LEFT from 0,1");
        } catch (RuntimeException rte) {}

    }

    @Test
    public void testListPositionsWithContentOf() throws Exception {
        WorldState ws = new WorldState(10, 10);

        // Change some tiles
        Tile[] tiles = ws.getTiles();

        tiles[5] = new Tile(new Food());
        tiles[15] = new Tile(new Food());
        tiles[25] = new Tile(new Food());
        tiles[78] = new Tile(new Obstacle());
        tiles[88] = new Tile(new Obstacle());
        tiles[92] = new Tile(new Obstacle());
        tiles[98] = new Tile(new Obstacle());

        WorldState newWorld = new WorldState(ws.getWidth(), ws.getHeight(), tiles);

        int[] foodPositions = newWorld.listPositionsWithContentOf(Food.class);
        int[] obstaclePositions = newWorld.listPositionsWithContentOf(Obstacle.class);

        assertEquals(3, foodPositions.length);
        assertEquals(4, obstaclePositions.length);

        assertArrayEquals(new int[] {5, 15, 25}, foodPositions);
        assertArrayEquals(new int[] {78, 88, 92, 98}, obstaclePositions);
    }

    @Test
    public void testListEmptyPositions_AllEmpty() throws Exception {

        WorldState ws = new WorldState(10, 10);

        int[] emptyPositions = ws.listEmptyPositions();
        assertEquals(100, emptyPositions.length);
        assertArrayEquals(IntStream.range(0, 100).toArray(), emptyPositions);
    }

    @Test
    public void testListEmptyPositions_NotAllEmpty() throws Exception {

        WorldState ws = SnakeTestUtil.createWorld(Food.class, 10, 10, new int[] { 50 });

        int[] emptyPositions = ws.listEmptyPositions();
        assertEquals(99, emptyPositions.length);

        assertArrayEquals(IntStream.range(0, 100).filter( pos -> pos != 50).toArray(), emptyPositions);
    }

    @Test
    public void testListEmptyPositions() throws Exception {

        int[] foodPositions = new int[] { 2, 6, 85 };
        WorldState ws = SnakeTestUtil.createWorld(Food.class, 10, 10, foodPositions);

        int[] emptyPositions = IntStream.range(0, 100).filter( pos->
            !ArrayUtils.contains(foodPositions, pos)
        ).toArray();

        assertArrayEquals(emptyPositions, ws.listEmptyPositions());
    }

    @Test
    public void testListEmptyValidPositions() throws Exception {
        int[] foodPositions = new int[] { 2, 6, 90 };
        WorldState ws = SnakeTestUtil.createWorld(Food.class, 10, 10, foodPositions);

        SnakeHead h1 = new SnakeHead("h1", "p1", 16);
        SnakeHead h2 = new SnakeHead("h2", "p2", 84);

        ws = SnakeTestUtil.replaceWorldObjectAt(ws, h1, 16);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, h2, 84);

        int[] adjacentAndSnakeHead = new int[] {16, 84, 6,26,15,17,74,94,83,85};

        int[] validPositions = IntStream.range(0, 100).filter( pos->
                (!ArrayUtils.contains(foodPositions, pos) &&
                !ArrayUtils.contains(adjacentAndSnakeHead, pos))
        ).toArray();

        assertArrayEquals(validPositions, ws.listEmptyValidPositions());
    }

    @Test
    public void testListFoodPositions() throws Exception {

        int[] foodPositions = new int[] { 2, 6, 85 };
        WorldState ws = SnakeTestUtil.createWorld(Food.class, 10, 10, foodPositions);

        assertArrayEquals(foodPositions, ws.listFoodPositions());
    }

    @Test
    public void testListObstaclePositions() throws Exception {

        int[] obstaclePositions = new int[] { 8, 13, 23, 55, 87, 99 };
        WorldState ws = SnakeTestUtil.createWorld(Obstacle.class, 10, 10, obstaclePositions);

        assertArrayEquals(obstaclePositions, ws.listObstaclePositions());
    }

    @Test
    public void testListPositionsAdjacentToSnakeHeads() throws Exception {
        WorldState ws = new WorldState(15, 15);

        SnakeHead h1 = new SnakeHead("h1", "p1", 16);
        SnakeHead h2 = new SnakeHead("h2", "p2", 84);

        ws = SnakeTestUtil.replaceWorldObjectAt(ws, h1, 16);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, h2, 84);

        int[] illegalPos = ws.listPositionsAdjacentToSnakeHeads();

        assertEquals(8, illegalPos.length);
        assertThat(
                ArrayUtils.toObject(illegalPos),
                arrayContainingInAnyOrder(1,31,15,17,69,99,83,85));
    }

    @Test
    public void testListPositionsAdjacentToSnakeHeadsNearEdge() throws Exception {
        WorldState ws = new WorldState(15, 15);

        SnakeHead h1 = new SnakeHead("h1", "p1", 45);
        SnakeHead h2 = new SnakeHead("h2", "p2", 84);

        ws = SnakeTestUtil.replaceWorldObjectAt(ws, h1, 45);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, h2, 84);

        int[] illegalPos = ws.listPositionsAdjacentToSnakeHeads();

        assertEquals(7, illegalPos.length);
        assertThat(
                ArrayUtils.toObject(illegalPos),
                arrayContainingInAnyOrder(30,46,60,69,99,83,85));
    }

    @Test
    public void testHasAdjacentTileLeft() throws Exception {
        WorldState ws = new WorldState(15, 15);
        Direction direction = Direction.LEFT;
        assertFalse(ws.hasAdjacentTile(0,   direction));
        assertFalse(ws.hasAdjacentTile(150, direction));
        assertTrue(ws.hasAdjacentTile(151,  direction));
        assertTrue(ws.hasAdjacentTile(89,   direction));
        assertTrue(ws.hasAdjacentTile(224,  direction));
    }

    @Test
    public void testHasAdjacentTileRight() throws Exception {
        WorldState ws = new WorldState(15, 15);
        Direction direction = Direction.RIGHT;
        assertFalse(ws.hasAdjacentTile(224,   direction));
        assertFalse(ws.hasAdjacentTile(59, direction));
        assertTrue(ws.hasAdjacentTile(82,  direction));
        assertTrue(ws.hasAdjacentTile(0,   direction));
        assertTrue(ws.hasAdjacentTile(105,  direction));
    }

    @Test
    public void testHasAdjacentTileUp() throws Exception {
        WorldState ws = new WorldState(15, 15);
        Direction direction = Direction.UP;
        assertFalse(ws.hasAdjacentTile(0,   direction));
        assertFalse(ws.hasAdjacentTile(12, direction));
        assertFalse(ws.hasAdjacentTile(14, direction));
        assertTrue(ws.hasAdjacentTile(15,  direction));
        assertTrue(ws.hasAdjacentTile(82,  direction));
        assertTrue(ws.hasAdjacentTile(195,   direction));
        assertTrue(ws.hasAdjacentTile(224,  direction));
    }

    @Test
    public void testHasAdjacentTileDown() throws Exception {
        WorldState ws = new WorldState(15, 15);
        Direction direction = Direction.DOWN;
        assertFalse(ws.hasAdjacentTile(210,   direction));
        assertFalse(ws.hasAdjacentTile(216, direction));
        assertFalse(ws.hasAdjacentTile(224, direction));
        assertTrue(ws.hasAdjacentTile(3,  direction));
        assertTrue(ws.hasAdjacentTile(97,   direction));
        assertTrue(ws.hasAdjacentTile(150,  direction));
    }

    @Test
    public void testListAdjacentTilesMiddle() throws Exception {
        WorldState ws = new WorldState(15, 15);
        int[] adjacent = ws.listAdjacentTiles(112);

        assertEquals(4, adjacent.length);
        assertThat(ArrayUtils.toObject(adjacent), arrayContainingInAnyOrder(111,113,97,127));
    }

    @Test
    public void testListAdjacentTilesLeftWall() throws Exception {
        WorldState ws = new WorldState(15, 15);
        int[] adjacent = ws.listAdjacentTiles(135);

        assertEquals(3, adjacent.length);
        assertThat(ArrayUtils.toObject(adjacent), arrayContainingInAnyOrder(120,136,150));
    }

    @Test
    public void testListAdjacentTilesRightWall() throws Exception {
        WorldState ws = new WorldState(15, 15);
        int[] adjacent = ws.listAdjacentTiles(59);

        assertEquals(3, adjacent.length);
        assertThat(ArrayUtils.toObject(adjacent), arrayContainingInAnyOrder(44,58,74));
    }

    @Test
    public void testListAdjacentTilesTopWall() throws Exception {
        WorldState ws = new WorldState(15, 15);
        int[] adjacent = ws.listAdjacentTiles(10);

        assertEquals(3, adjacent.length);
        assertThat(ArrayUtils.toObject(adjacent), arrayContainingInAnyOrder(25,9,11));
    }

    @Test
    public void testListAdjacentTilesBottomWall() throws Exception {
        WorldState ws = new WorldState(15, 15);
        int[] adjacent = ws.listAdjacentTiles(217);

        assertEquals(3, adjacent.length);
        assertThat(ArrayUtils.toObject(adjacent), arrayContainingInAnyOrder(202,216,218));
    }

    @Test
    public void testListAdjacentTilesTopLeftCorner() throws Exception {
        WorldState ws = new WorldState(15, 15);
        int[] adjacent = ws.listAdjacentTiles(0);

        assertEquals(2, adjacent.length);
        assertThat(ArrayUtils.toObject(adjacent), arrayContainingInAnyOrder(1,15));
    }

    @Test
    public void testListAdjacentTilesTopRightCorner() throws Exception {
        WorldState ws = new WorldState(15, 15);
        int[] adjacent = ws.listAdjacentTiles(14);

        assertEquals(2, adjacent.length);
        assertThat(ArrayUtils.toObject(adjacent), arrayContainingInAnyOrder(13,29));
    }

    @Test
    public void testListAdjacentTilesBottomLeftCorner() throws Exception {
        WorldState ws = new WorldState(15, 15);
        int[] adjacent = ws.listAdjacentTiles(210);

        assertEquals(2, adjacent.length);
        assertThat(ArrayUtils.toObject(adjacent), arrayContainingInAnyOrder(195,211));
    }

    @Test
    public void testListAdjacentTilesBottomRightCorner() throws Exception {
        WorldState ws = new WorldState(15, 15);
        int[] adjacent = ws.listAdjacentTiles(224);

        assertEquals(2, adjacent.length);
        assertThat(ArrayUtils.toObject(adjacent), arrayContainingInAnyOrder(209,223));
    }

    @Test
    public void testGetSnakeSpread() throws Exception {
        WorldState ws = new WorldState(10, 10);

        SnakePart[] parts = SnakeTestUtil.createSnake("test", "id", 13,23,24,25,35);
        ws = SnakeTestUtil.addSnake(ws, parts);

        int[] snakeSpread = ws.getSnakeSpread((SnakeHead)parts[0]);

        assertEquals(5, snakeSpread.length);
        assertThat(ArrayUtils.toObject(snakeSpread), arrayContaining(13,23,24,25,35));
    }

    @Test
    public void testGetSnakeHeadForBodyAt() throws Exception {
        WorldState ws = new WorldState(10, 10);

        SnakePart[] parts = SnakeTestUtil.createSnake("test", "id", 13,23,24,25,35);
        ws = SnakeTestUtil.addSnake(ws, parts);

        SnakeHead head = ws.getSnakeHeadForBodyAt(25);

        assertEquals(parts[0], head);
    }

    @Test(expected = RuntimeException.class)
    public void testGetSnakeHeadForBodyAtNonSnakePosition() throws Exception {
        WorldState ws = new WorldState(10, 10);

        SnakePart[] parts = SnakeTestUtil.createSnake("test", "id", 63, 64, 65, 55, 45);
        ws = SnakeTestUtil.addSnake(ws, parts);
        ws.getSnakeHeadForBodyAt(101);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetSnakeHeadForBodyAtWithSnakeWithoutHead() throws Exception {
        WorldState ws = new WorldState(10, 10);

        SnakePart[] parts = SnakeTestUtil.createSnake("test", "id", 70, 71, 72, 73, 74, 84, 94);

        ws = SnakeTestUtil.addSnake(ws, ArrayUtils.subarray(parts, 1, 6));
        ws.getSnakeHeadForBodyAt(73);
    }

    @Test @Ignore
    public void testPrintCoordinatePosition() {
        int size = 10;
        int counter = 0;
        for (int x=0; x<size; x++) {
            for (int y=0; y<size; y++) {
                System.out.printf("%03d ", counter++);
            }
            System.out.println("\n");
        }
    }
}