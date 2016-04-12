package se.cygni.game.transformation;

import org.junit.Test;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakeHead;

import static org.junit.Assert.assertEquals;

/**
 * @author Alan Tibbetts
 * @since 12/04/16
 */
public class DecrementTailProtectionTest {

    @Test
    public void testTransform() {
        SnakeHead snakeA = new SnakeHead("a", "a", 2);
        snakeA.setTailProtectedForGameTicks(3);

        Tile[] tiles = new WorldState(3, 3).getTiles();
        tiles[2] = new Tile(snakeA);
        WorldState worldState = new WorldState(3, 3, tiles);

        DecrementTailProtection decrementTailProtection = new DecrementTailProtection();

        WorldState updatedWorldState = decrementTailProtection.transform(worldState);
        SnakeHead updatedSnakeHead = updatedWorldState.getSnakeHeadById("a");
        assertEquals(2, updatedSnakeHead.getTailProtectedForGameTicks());
    }

    @Test
    public void testTransformWithZeroCount() {
        SnakeHead snakeA = new SnakeHead("a", "a", 2);

        Tile[] tiles = new WorldState(3, 3).getTiles();
        tiles[2] = new Tile(snakeA);
        WorldState worldState = new WorldState(3, 3, tiles);

        DecrementTailProtection decrementTailProtection = new DecrementTailProtection();

        WorldState updatedWorldState = decrementTailProtection.transform(worldState);
        SnakeHead updatedSnakeHead = updatedWorldState.getSnakeHeadById("a");
        assertEquals(0, updatedSnakeHead.getTailProtectedForGameTicks());
    }
}
