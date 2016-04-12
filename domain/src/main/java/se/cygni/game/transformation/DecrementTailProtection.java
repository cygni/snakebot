package se.cygni.game.transformation;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakeHead;

public class DecrementTailProtection implements WorldTransformation {

    @Override
    public WorldState transform(WorldState currentWorld) {

        Tile[] tiles = currentWorld.getTiles();
        int[] headPositions = currentWorld.listPositionsWithContentOf(SnakeHead.class);

        for (int headPos : headPositions) {
            SnakeHead snakeHead = (SnakeHead)tiles[headPos].getContent();
            snakeHead.decrementTailProtection();
        }

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
