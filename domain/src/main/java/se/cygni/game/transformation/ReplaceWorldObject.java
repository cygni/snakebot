package se.cygni.game.transformation;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.WorldObject;

/**
 * Adds a Food object at random free Tile
 */
public class ReplaceWorldObject implements WorldTransformation {

    private final WorldObject worldObject;
    private final int position;

    public ReplaceWorldObject(WorldObject worldObject, int position) {
        this.worldObject = worldObject;
        this.position = position;
    }

    @Override
    public WorldState transform(WorldState currentWorld) {

        Tile[] tiles = currentWorld.getTiles();
        tiles[position] = new Tile(worldObject);

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
