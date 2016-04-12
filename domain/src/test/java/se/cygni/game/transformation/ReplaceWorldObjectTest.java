package se.cygni.game.transformation;

import org.junit.Test;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.Food;

import static org.junit.Assert.assertTrue;

/**
 * @author Alan Tibbetts
 * @since 12/04/16
 */
public class ReplaceWorldObjectTest {

    @Test
    public void testTransform() {
        WorldState worldState = new WorldState(3, 3);
        Tile tile = worldState.getTile(6);
        assertTrue(tile.getContent() instanceof Empty);

        ReplaceWorldObject replaceWorldObject = new ReplaceWorldObject(new Food(), 6);
        WorldState updatedWorldState = replaceWorldObject.transform(worldState);
        Tile updatedTile = updatedWorldState.getTile(6);
        assertTrue(updatedTile.getContent() instanceof Food);
    }

    @Test
    public void testTransformSameType() {
        WorldState worldState = new WorldState(3, 3);
        Tile tile = worldState.getTile(6);
        assertTrue(tile.getContent() instanceof Empty);

        ReplaceWorldObject replaceWorldObject = new ReplaceWorldObject(new Empty(), 6);
        WorldState updatedWorldState = replaceWorldObject.transform(worldState);
        Tile updatedTile = updatedWorldState.getTile(6);
        assertTrue(updatedTile.getContent() instanceof Empty);
    }
}
