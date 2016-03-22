package se.cygni.game.transformation;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.game.worldobject.SnakePart;

/**
 * Adds a Food object at random free Tile
 */
public class TailNibbled implements WorldTransformation {

    private final String snakeId;
    private final int position;

    public TailNibbled(String snakeId, int position) {
        this.snakeId = snakeId;
        this.position = position;
    }

    @Override
    public WorldState transform(WorldState currentWorld) {

        SnakeHead head = currentWorld.getSnakeHeadById(snakeId);
        int[] snakeSpread = currentWorld.getSnakeSpread(head);

        if (snakeSpread[snakeSpread.length-1] != position) {
            throw new IllegalStateException("Supplied position was not the snakes tail");
        }


        Tile[] tiles = currentWorld.getTiles();
        tiles[position] = new Tile();

        int previousPartPosition = snakeSpread[snakeSpread.length-2];
        SnakePart previousPart = (SnakePart)tiles[previousPartPosition].getContent();
        previousPart.setNextSnakePart(null);

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }
}
