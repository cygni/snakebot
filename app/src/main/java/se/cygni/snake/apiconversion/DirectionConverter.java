package se.cygni.snake.apiconversion;

import se.cygni.game.enums.Direction;
import se.cygni.snake.api.model.SnakeDirection;

public class DirectionConverter {

    public static Direction toDirection(SnakeDirection snakeDirection) {
        switch (snakeDirection) {
            case UP: return Direction.UP;
            case DOWN: return Direction.DOWN;
            case LEFT: return Direction.LEFT;
            case RIGHT: return Direction.RIGHT;
        }
        throw new RuntimeException("Could not convert SnakeDirection: " + snakeDirection + " to Direction");
    }

    public static SnakeDirection toSnakeDirection(Direction direction) {
        switch (direction) {
            case UP:    return SnakeDirection.UP;
            case DOWN:  return SnakeDirection.DOWN;
            case LEFT:  return SnakeDirection.LEFT;
            case RIGHT: return SnakeDirection.RIGHT;
        }
        throw new RuntimeException("Could not convert Direction: " + direction + " to SnakeDirection");
    }
}
