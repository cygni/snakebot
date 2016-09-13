package se.cygni.game.transformation;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakeHead;

import java.util.stream.IntStream;

public class DecrementTailProtection implements WorldTransformation {

    @Override
    public WorldState transform(WorldState currentWorld) {

        Tile[] tiles = currentWorld.getTiles();
        int[] headPositions = currentWorld.listPositionsWithContentOf(SnakeHead.class);

        IntStream.of(headPositions).forEach( headPosition -> {
            SnakeHead snakeHead = (SnakeHead)tiles[headPosition].getContent();
            snakeHead.decrementTailProtection();
        });

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
