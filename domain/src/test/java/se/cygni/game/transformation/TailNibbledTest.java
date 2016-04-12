package se.cygni.game.transformation;

import org.junit.Test;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.worldobject.SnakeBody;
import se.cygni.game.worldobject.SnakeHead;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Alan Tibbetts
 * @since 12/04/16
 */
public class TailNibbledTest {

    @Test
    public void testTransform() throws TransformationException {
        WorldState worldState = createWorldState();

        TailNibbled tailNibbled = new TailNibbled("a", 3, 3);
        WorldState updatedWorldState = tailNibbled.transform(worldState);

        SnakeHead snakeA = updatedWorldState.getSnakeHeadById("a");
        assertEquals(4, snakeA.getTailProtectedForGameTicks());
        assertNull(snakeA.getNextSnakePart());
    }

    @Test(expected = TransformationException.class)
    public void testTransformWrongPosition() throws TransformationException {
        WorldState worldState = createWorldState();

        TailNibbled tailNibbled = new TailNibbled("a", 5, 3);
        WorldState updatedWorldState = tailNibbled.transform(worldState);
    }

    private WorldState createWorldState() {
        Tile[] tiles = new WorldState(3,3).getTiles();

        SnakeBody tailA = new SnakeBody("a", null, 3);
        tiles[tailA.getPosition()] = new Tile(tailA);

        SnakeHead headA = new SnakeHead("a", "a", 0);
        tiles[headA.getPosition()] = new Tile(headA);
        headA.setNextSnakePart(tailA);

        SnakeBody tailB = new SnakeBody("b", null, 4);
        tiles[tailB.getPosition()] = new Tile(tailB);

        SnakeHead headB = new SnakeHead("b", "b", 4);
        tiles[headB.getPosition()] = new Tile(headB);
        headB.setNextSnakePart(tailB);

        return new WorldState(3, 3, tiles);
    }
}
