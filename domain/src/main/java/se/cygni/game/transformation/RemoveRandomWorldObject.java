package se.cygni.game.transformation;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.WorldObject;

import java.util.Random;

/**
 * Adds a Food object at random free Tile
 */
public class RemoveRandomWorldObject<T extends WorldObject> implements WorldTransformation {

    private final Class<T> worldObjectType;
    private RandomGenerator randomGenerator = RandomGeneratorFactory.createRandomGenerator(new Random());

    public RemoveRandomWorldObject(Class<T> worldObjectType) {
        this.worldObjectType = worldObjectType;
    }

    @Override
    public WorldState transform(WorldState currentWorld) {

        int[] positionsWithContentOfType = currentWorld.listPositionsWithContentOf(worldObjectType);
        if (positionsWithContentOfType.length == 0)
            return currentWorld;

        int randomPosition = positionsWithContentOfType[randomGenerator.nextInt(positionsWithContentOfType.length)];

        Tile[] tiles = currentWorld.getTiles();
        tiles[randomPosition] = new Tile(new Empty());

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
