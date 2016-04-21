package se.cygni.game.transformation;

import org.apache.commons.lang3.ArrayUtils;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.random.XORShiftRandom;
import se.cygni.game.worldobject.SnakePart;
import se.cygni.game.worldobject.WorldObject;

/**
 * Adds a WorldObject at random free Tile
 */
public class AddWorldObjectAtRandomPosition implements WorldTransformation {

    private final WorldObject worldObject;
    private XORShiftRandom random = new XORShiftRandom();

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

        int randomPosition = validPositions[random.nextInt(validPositions.length)];

        Tile[] tiles = currentWorld.getTiles();
        tiles[randomPosition] = new Tile(worldObject);

        if (worldObject instanceof SnakePart) {
            SnakePart snakePart = (SnakePart)worldObject;
            snakePart.setPosition(randomPosition);
        }

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
