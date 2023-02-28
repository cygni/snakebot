package se.cygni.game.transformation;

import java.util.Objects;
import se.cygni.game.Tile;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.exception.ObstacleCollision;
import se.cygni.game.exception.SnakeCollision;
import se.cygni.game.exception.TransformationException;
import se.cygni.game.exception.WallCollision;
import se.cygni.game.worldobject.*;

public class MoveSnake implements WorldTransformation {

    private SnakeHead snakeHead;
    private Direction direction;
    private boolean forceGrowth;
    private boolean consumedFood;
    private boolean growthExecuted;

    public MoveSnake(SnakeHead snakeHead, Direction direction) {
        this(snakeHead, direction, false);
    }

    public MoveSnake(SnakeHead snakeHead, Direction direction, boolean forceGrowth) {
        this.snakeHead = snakeHead;
        this.direction = direction;
        this.forceGrowth = forceGrowth;
    }

    @Override
    public WorldState transform(WorldState currentWorld) throws TransformationException {
        if (currentWorld.listSnakeIds().size() > 1) {
            throw new TransformationException("Expected only one snake in this world transformation");
        }
        if (snakeHead == null) {
            throw new TransformationException("SnakeHead is null!!");
        }

        if (direction == null) {
            throw new TransformationException("Direction is null!");
        }


        int snakeHeadPos = snakeHead.getPosition();
        int targetSnakePos = 0;

        // Snake tries to move out of bounds
        try {
            targetSnakePos = currentWorld.getPositionForAdjacent(snakeHeadPos, direction);
        } catch (RuntimeException re) {
            throw new WallCollision(snakeHeadPos);
        }

        // Target tile is not empty, check what's in it (remember that this
        // move is in a World where only this Snake exists).
        if (!currentWorld.isTileEmpty(targetSnakePos)) {

            Tile targetTile = currentWorld.getTile(targetSnakePos);
            WorldObject targetContent = targetTile.getContent();

            if (targetContent instanceof Obstacle) {
                throw new ObstacleCollision(targetSnakePos);
            }

            // Snakes are allowed to follow their own tail as long as
            // they don't grow or their tail comes after their head.
            // Collision with an opponent's tail cannot happen here because
            // this transformer expects only one snake in the world.
            if (targetContent instanceof SnakePart &&
                (forceGrowth  ||
                !((SnakePart)targetContent).isTail() ||
                  snakeHead.getLength() == 2)) {
                throw new SnakeCollision(targetSnakePos, snakeHead);
            }

            if (targetContent instanceof Food) {
                consumedFood = true;
            }
        }

        Tile[] tiles = currentWorld.getTiles();

        growthExecuted = consumedFood || forceGrowth;
        updateSnakeBody(tiles, targetSnakePos, snakeHead, growthExecuted);

        return new WorldState(currentWorld.getWidth(), currentWorld.getHeight(), tiles);
    }

    public boolean isGrowthExecuted() {
        return growthExecuted;
    }

    public boolean isFoodConsumed() {
        return consumedFood;
    }

    private void updateSnakeBody(Tile[] tiles, int targetPosition, SnakePart snakePart, boolean grow) {
        int currentPosition = snakePart.getPosition();

        tiles[targetPosition] = new Tile(snakePart);
        snakePart.setPosition(targetPosition);

        if (snakePart.isTail()) {
            if (grow) {
                SnakeBody newTail = new SnakeBody(snakePart.getPlayerId(), currentPosition);
                tiles[currentPosition] = new Tile(newTail);
                snakePart.setNextSnakePart(newTail);
            } else {
                tiles[currentPosition] = new Tile();
            }
        } else {
            updateSnakeBody(tiles, currentPosition, snakePart.getNextSnakePart(), grow);
        }
    }
}
