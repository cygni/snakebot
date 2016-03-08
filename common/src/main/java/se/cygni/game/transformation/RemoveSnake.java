package se.cygni.game.transformation;

import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.exception.ObstacleCollision;
import se.cygni.game.exception.SnakeCollision;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.exception.WallCollision;
import se.cygni.game.worldobject.*;

public class RemoveSnake implements WorldTransformation {

    private SnakeHead snakeHead;

    public RemoveSnake(SnakeHead snakeHead) {
        this.snakeHead = snakeHead;
    }

    @Override
    public WorldState transform(WorldState currentWorld) throws TransformationException {
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
