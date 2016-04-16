package se.cygni.snake.player.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.api.model.*;

/**
 * @author Alan Tibbetts
 * @since 13/04/16
 */
public class PotentialDirection implements Comparable<PotentialDirection> {

    public static SnakeDirection[] POSSIBLE_DIRECTIONS = {SnakeDirection.UP, SnakeDirection.DOWN, SnakeDirection.LEFT, SnakeDirection.RIGHT};

    private static final Logger LOGGER = LoggerFactory.getLogger(PotentialDirection.class);

    static final int DEFAULT_KEEP_GOING_SCORE = 10;
    static final int DEFAULT_EMPTY_SCORE = 100;
    static final int DEFAULT_FOOD_SCORE = 400;
    static final int DEFAULT_AVOID_SCORE = -100;
    static final int DEFAULT_SNAKE_TAIL_SCORE = 0;
    static final int DEFAULT_OUT_OF_BOUNDS_SCORE = -100;

    private final SnakeDirection direction;

    private int keepGoingScore = DEFAULT_KEEP_GOING_SCORE;
    private int emptyScore = DEFAULT_EMPTY_SCORE;
    private int foodScore = DEFAULT_FOOD_SCORE;
    private int avoidScore = DEFAULT_AVOID_SCORE;
    private int snakeTailScore = DEFAULT_SNAKE_TAIL_SCORE;
    private int outOfBoundsScore = DEFAULT_OUT_OF_BOUNDS_SCORE;

    private Integer score = 0;

    public PotentialDirection(final SnakeDirection direction) {
        this.direction = direction;
    }

    public SnakeDirection getDirection() {
        return direction;
    }

    public int getScore() {
        return score;
    }

    public void addTile(final Map gameMap, final TileContent tileContent, final int distanceFromSnake, final String playerId) {
        int scoreToAdd = 0;
        switch (tileContent.getContent()) {
            case "empty":
                scoreToAdd = getEmptyScore() / distanceFromSnake;
                break;
            case "food":
                scoreToAdd = getFoodScore() / distanceFromSnake;
                break;
            case "snakebody":
                if (getSnakeTailScore() > 0 && tileContent instanceof MapSnakeBody) {
                    MapSnakeBody mapSnakeBody = (MapSnakeBody) tileContent;
                    if (mapSnakeBody.isTail() && !mapSnakeBody.getPlayerId().equals(playerId)) {
                        SnakeInfo snake = findSnake(gameMap, mapSnakeBody.getPlayerId());
                        if (snake != null && snake.getTailProtectedForGameTicks() > 0) {
                            scoreToAdd = -scoreToAdd;  // Don't eat protected snakes!
                        } else {
                            scoreToAdd = getSnakeTailScore();  // Don't care how far away, we want to eat!
                        }
                    } else {  // Not the tail
                        scoreToAdd = getAvoidScore() / distanceFromSnake;
                    }
                } else { // SnakeHead
                    scoreToAdd = (2 * getAvoidScore()) / distanceFromSnake;  // Really want to avoid heads!
                }
                break;
            case "obstacle":
                scoreToAdd = getAvoidScore() / distanceFromSnake;
                break;
            default:
                break;
        }
        this.score += scoreToAdd;
        LOGGER.trace("D: {}, C: {}, S: {}, Score: {}", distanceFromSnake, tileContent.getContent(), scoreToAdd, this.toString());
    }

    private SnakeInfo findSnake(final Map gameMap, final String playerId) {
        for (SnakeInfo snakeInfo : gameMap.getSnakeInfos()) {
            if (snakeInfo.getId().equals(playerId)) {
                return snakeInfo;
            }
        }
        return null;
    }

    @Override
    public int compareTo(PotentialDirection o) {
        return this.score.compareTo(o.getScore());
    }

    public void goingThisWayAnyway() {
        this.score = this.score + getKeepGoingScore();
    }

    public int getKeepGoingScore() {
        return keepGoingScore;
    }

    public void setKeepGoingScore(int keepGoingScore) {
        this.keepGoingScore = keepGoingScore;
    }

    public int getEmptyScore() {
        return emptyScore;
    }

    public void setEmptyScore(int emptyScore) {
        this.emptyScore = emptyScore;
    }

    public int getFoodScore() {
        return foodScore;
    }

    public void setFoodScore(int foodScore) {
        this.foodScore = foodScore;
    }

    public int getAvoidScore() {
        return avoidScore;
    }

    public void setAvoidScore(int avoidScore) {
        this.avoidScore = avoidScore;
    }

    public int getSnakeTailScore() {
        return snakeTailScore;
    }

    public void setSnakeTailScore(int snakeTailScore) {
        this.snakeTailScore = snakeTailScore;
    }

    public int getOutOfBoundsScore() {
        return outOfBoundsScore;
    }

    public void setOutOfBoundsScore(int outOfBoundsScore) {
        this.outOfBoundsScore = outOfBoundsScore;
    }

    public void applyOutOfBoundsScore() {
        this.score = this.score + getOutOfBoundsScore();
    }

    public static boolean isOppositeDirection(final SnakeDirection currentDirection, final SnakeDirection potentialDirection) {
        boolean oppositeDirection = false;
        if (currentDirection != null) {
            switch (currentDirection) {
                case UP:
                    oppositeDirection = potentialDirection == SnakeDirection.DOWN;
                    break;
                case DOWN:
                    oppositeDirection = potentialDirection == SnakeDirection.UP;
                    break;

                case LEFT:
                    oppositeDirection = potentialDirection == SnakeDirection.RIGHT;
                    break;

                case RIGHT:
                    oppositeDirection = potentialDirection == SnakeDirection.LEFT;
                    break;

                default:
                    throw new RuntimeException("Unknown Direction: " + potentialDirection.toString());
            }
        }
        return oppositeDirection;
    }

    @Override
    public String toString() {
        return "PotentialDirection{" +
                "direction=" + direction +
                ", score=" + score +
                '}';
    }
}
