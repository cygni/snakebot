package se.cygni.game.transformation;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.game.worldobject.WorldObject;

import java.util.Random;

/**
 * Adds a WorldObject at random free Tile
 */
public class AddWorldObjectAtRandomPosition implements WorldTransformation {

    private final WorldObject worldObject;
    private RandomGenerator randomGenerator = RandomGeneratorFactory.createRandomGenerator(new Random());

    public AddWorldObjectAtRandomPosition(WorldObject worldObject) {
        this.worldObject = worldObject;
    }

    @Override
    public WorldState transform(WorldState currentWorld) {

        int[] emptyPositions = currentWorld.listEmptyPositions();
        if (emptyPositions.length == 0) {
            return currentWorld;
        }

        int[] illegalPositions = currentWorld.listPositionsAdjacentToSnakeHeads();

        int[] validPositions = ArrayUtils.removeElements(emptyPositions, illegalPositions);
        if (validPositions.length == 0) {
            return currentWorld;
        }

        int randomPosition = validPositions[randomGenerator.nextInt(validPositions.length)];

        Tile[] tiles = currentWorld.getTiles();
        tiles[randomPosition] = new Tile(worldObject);

        if (worldObject instanceof SnakeHead) {
            SnakeHead snakeHead = (SnakeHead)worldObject;
            snakeHead.setPosition(randomPosition);
        }

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
