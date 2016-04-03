package se.cygni.game.transformation;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.game.worldobject.SnakePart;

public class RemoveSnake implements WorldTransformation {

    private SnakeHead snakeHead;
    private String snakeId;

    public RemoveSnake(SnakeHead snakeHead) {
        this.snakeHead = snakeHead;
    }

    public RemoveSnake(String snakeId) {
        this.snakeId = snakeId;
    }

    @Override
    public WorldState transform(WorldState currentWorld) throws TransformationException {
        if (snakeHead == null) {
            snakeHead = currentWorld.getSnakeHeadById(snakeId);
        }

        Tile[] tiles = currentWorld.getTiles();

        tiles[snakeHead.getPosition()] = new Tile();

        if (!snakeHead.isTail())
            removeSnakeBody(tiles, snakeHead.getNextSnakePart());

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }

    private void removeSnakeBody(Tile[] tiles, SnakePart snakePart) {

        int currentPosition = snakePart.getPosition();
        tiles[currentPosition] = new Tile();

        if (!snakePart.isTail()) {
            removeSnakeBody(tiles, snakePart.getNextSnakePart());
        }
    }
}
