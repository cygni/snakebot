package se.cygni.game.transformation;

import org.junit.Test;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.SnakeBody;
import se.cygni.game.worldobject.SnakeHead;

import static org.junit.Assert.*;

/**
 * @author Alan Tibbetts
 * @since 12/04/16
 */
public class KeepOnlySnakeWithIdTest {

    @Test
    public void testTransformSnakeHeadsOnly() throws TransformationException {
        Tile[] tiles = new WorldState(3, 3).getTiles();
        tiles[2] = new Tile(new SnakeHead("a", "a", 2));
        tiles[6] = new Tile(new SnakeHead("b", "b", 6));
        tiles[8] = new Tile(new SnakeHead("b", "b", 6));

        WorldState worldState = new WorldState(3, 3, tiles);

        KeepOnlySnakeWithId keepOnlySnakeWithId = new KeepOnlySnakeWithId("a");
        WorldState updatedWorldState = keepOnlySnakeWithId.transform(worldState);

        assertNotNull(updatedWorldState.getSnakeHeadById("a"));

        try {
            updatedWorldState.getSnakeHeadById("b");
            fail("Snake 'b' should have been removed from the board");
        } catch (IllegalArgumentException iae) {
            // What we expect!
        }

        try {
            updatedWorldState.getSnakeHeadById("c");
            fail("Snake 'c' should have been removed from the board");
        } catch (IllegalArgumentException iae) {
            // What we expect!
        }
    }

    @Test(expected = TransformationException.class)
    public void testTransformCheckingNullId() throws TransformationException {
        Tile[] tiles = new WorldState(3, 3).getTiles();
        tiles[2] = new Tile(new SnakeHead("a", "a", 2));
        tiles[6] = new Tile(new SnakeHead("b", "b", 6));
        tiles[8] = new Tile(new SnakeHead("b", "b", 6));

        WorldState worldState = new WorldState(3, 3, tiles);

        KeepOnlySnakeWithId keepOnlySnakeWithId = new KeepOnlySnakeWithId(null);
        WorldState updatedWorldState = keepOnlySnakeWithId.transform(worldState);
    }

    @Test
    public void testTransformFullSnakes() throws TransformationException {
        Tile[] tiles = new WorldState(6, 6).getTiles();

        createSnake("a", "a", 6, tiles);
        createSnake("b", "b", 9, tiles);
        createSnake("c", "c", 11, tiles);

        WorldState worldState = new WorldState(6, 6, tiles);

        KeepOnlySnakeWithId keepOnlySnakeWithId = new KeepOnlySnakeWithId("a");
        WorldState updatedWorldState = keepOnlySnakeWithId.transform(worldState);
        Tile[] updatedTiles = updatedWorldState.getTiles();

        assertNotNull(updatedWorldState.getSnakeHeadById("a"));

        assertTrue(updatedTiles[6].getContent() instanceof SnakeHead);
        assertTrue(updatedTiles[12].getContent() instanceof SnakeBody);
        assertTrue(updatedTiles[18].getContent() instanceof SnakeBody);
        assertTrue(updatedTiles[24].getContent() instanceof SnakeBody);

        try {
            updatedWorldState.getSnakeHeadById("b");
            fail("Snake 'b' should have been removed from the board");
        } catch (IllegalArgumentException iae) {
            // What we expect!
        }

        assertTrue(updatedTiles[9].getContent() instanceof Empty);
        assertTrue(updatedTiles[15].getContent() instanceof Empty);
        assertTrue(updatedTiles[21].getContent() instanceof Empty);
        assertTrue(updatedTiles[27].getContent() instanceof Empty);

        try {
            updatedWorldState.getSnakeHeadById("c");
            fail("Snake 'c' should have been removed from the board");
        } catch (IllegalArgumentException iae) {
            // What we expect!
        }

        assertTrue(updatedTiles[11].getContent() instanceof Empty);
        assertTrue(updatedTiles[17].getContent() instanceof Empty);
        assertTrue(updatedTiles[23].getContent() instanceof Empty);
        assertTrue(updatedTiles[29].getContent() instanceof Empty);
    }

    private void createSnake(final String name, final String id, final int position, final Tile[] tiles) {
        SnakeBody body3 = new SnakeBody(id, null, position + 18);
        tiles[body3.getPosition()] = new Tile(body3);

        SnakeBody body2 = new SnakeBody(id, body3, position + 12);
        tiles[body2.getPosition()] = new Tile(body2);

        SnakeBody body1 = new SnakeBody(id, body2, position + 6);
        tiles[body1.getPosition()] = new Tile(body1);

        SnakeHead snakeHead = new SnakeHead(name, id, position);
        tiles[snakeHead.getPosition()] = new Tile(snakeHead);
        snakeHead.setNextSnakePart(body1);
    }
}
