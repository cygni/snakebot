package se.cygni.game.transformation;

import org.junit.Test;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.exception.ObstacleCollision;
import se.cygni.game.exception.SnakeCollision;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.exception.WallCollision;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.worldobject.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MoveSnakeTest {

    @Test
    public void testSimpleMoveRight() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 16;

        SnakeHead head = new SnakeHead("test", "id", startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        MoveSnake moveSnake = new MoveSnake(head, Direction.RIGHT);
        ws = moveSnake.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());
        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(SnakeHead.class));
    }

    @Test
    public void testSimpleMoveLeft() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 14;

        SnakeHead head = new SnakeHead("test", "id", startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        MoveSnake moveSnake = new MoveSnake(head, Direction.LEFT);
        ws = moveSnake.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());
        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(SnakeHead.class));
    }

    @Test
    public void testSimpleMoveUp() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 5;

        SnakeHead head = new SnakeHead("test", "id", startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        MoveSnake moveSnake = new MoveSnake(head, Direction.UP);
        ws = moveSnake.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());
        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(SnakeHead.class));
    }

    @Test
    public void testSimpleMoveDown() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 25;

        SnakeHead head = new SnakeHead("test", "id", startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        MoveSnake moveSnake = new MoveSnake(head, Direction.DOWN);
        ws = moveSnake.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());
        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(SnakeHead.class));
    }

    @Test
    public void testMultiSegmentMoveRight() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 16;

        SnakeHead head = new SnakeHead("test", "id", startPos);
        SnakeBody body = new SnakeBody("id", 25);
        head.setNextSnakePart(body);


        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body, 25);
        MoveSnake moveSnake = new MoveSnake(head, Direction.RIGHT);
        ws = moveSnake.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());
        assertEquals(startPos, body.getPosition());

        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(SnakeHead.class));
        assertArrayEquals(new int[]{startPos}, ws.listPositionsWithContentOf(SnakeBody.class));
    }

    @Test(expected = WallCollision.class)
    public void testWallCollision() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 95;

        SnakeHead head = new SnakeHead("test", "id", startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        MoveSnake moveSnake = new MoveSnake(head, Direction.DOWN);
        moveSnake.transform(ws);
    }

    @Test(expected = ObstacleCollision.class)
    public void testObstacleCollision() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 55;
        int obstaclePos = 56;

        SnakeHead head = new SnakeHead("test", "id", startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, new Obstacle(), obstaclePos);
        MoveSnake moveSnake = new MoveSnake(head, Direction.RIGHT);
        moveSnake.transform(ws);
    }

    @Test(expected = SnakeCollision.class)
    public void testSnakeCollisionWithSelf() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int[] positions = {12,2,3,13,23};
        SnakePart[] snake = SnakeTestUtil.createSnake("test", "id", positions);
        ws = SnakeTestUtil.addSnake(ws, snake);
        SnakeHead head = ws.getSnakeHeadForBodyAt(positions[0]);
        MoveSnake moveSnake = new MoveSnake(head, Direction.RIGHT);
        moveSnake.transform(ws);
    }

    @Test
    public void testMoveRightAndGrow() throws Exception {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;
        int expectedEndPos = 16;

        SnakeHead head = new SnakeHead("test", "id", startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, new Food(), expectedEndPos);
        MoveSnake moveSnake = new MoveSnake(head, Direction.RIGHT);
        ws = moveSnake.transform(ws);

        assertEquals(expectedEndPos, head.getPosition());

        assertArrayEquals(new int[]{expectedEndPos}, ws.listPositionsWithContentOf(SnakeHead.class));
        assertArrayEquals(new int[]{startPos}, ws.listPositionsWithContentOf(SnakeBody.class));
        assertArrayEquals(new int[]{}, ws.listFoodPositions());

    }

    @Test(expected = TransformationException.class)
    public void testMoveSnakeIsNull() throws TransformationException {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;

        SnakeHead head = null;
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        MoveSnake moveSnake = new MoveSnake(head, Direction.DOWN);
        ws = moveSnake.transform(ws);
    }

    @Test(expected = TransformationException.class)
    public void testMoveDirectionIsNull() throws TransformationException {
        WorldState ws = new WorldState(10, 10);

        int startPos = 15;

        SnakeHead head = new SnakeHead("test", "id", startPos);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, startPos);
        MoveSnake moveSnake = new MoveSnake(head, null);
        ws = moveSnake.transform(ws);
    }

    @Test
    public void testMoveFollowTail() throws Exception {
        WorldState ws = new WorldState(10, 10);
        int[] positions = {55, 45, 46, 56};
        SnakePart[] snake = SnakeTestUtil.createSnake("test", "id", positions);
        ws = SnakeTestUtil.addSnake(ws, snake);
        SnakeHead head = ws.getSnakeHeadForBodyAt(positions[0]);
        MoveSnake moveSnake = new MoveSnake(head, Direction.RIGHT, false);
        moveSnake.transform(ws);
    }

    @Test(expected = SnakeCollision.class)
    public void testSnakeCollisionFollowTailGrowth() throws Exception {
        WorldState ws = new WorldState(10, 10);
        int[] positions = {55, 45, 46, 56};
        SnakePart[] snake = SnakeTestUtil.createSnake("test", "id", positions);
        ws = SnakeTestUtil.addSnake(ws, snake);
        SnakeHead head = ws.getSnakeHeadForBodyAt(positions[0]);
        MoveSnake moveSnake = new MoveSnake(head, Direction.RIGHT, true);
        moveSnake.transform(ws);
    }

    @Test(expected = SnakeCollision.class)
    public void testSnakeCollisionTurnAround() throws Exception {
        WorldState ws = new WorldState(10, 10);
        int[] positions = {55, 56};
        SnakePart[] snake = SnakeTestUtil.createSnake("test", "id", positions);
        ws = SnakeTestUtil.addSnake(ws, snake);
        SnakeHead head = ws.getSnakeHeadForBodyAt(positions[0]);
        MoveSnake moveSnake = new MoveSnake(head, Direction.RIGHT, false);
        moveSnake.transform(ws);
    }

}