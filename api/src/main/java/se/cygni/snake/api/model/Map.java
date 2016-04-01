package se.cygni.snake.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class Map extends GameMessage {
    final int width, height;
    final long worldTick;

    final SnakeInfo[] snakeInfos;

    // List of positions containing Food
    final int[] foodPositions;

    // List of positions containing Obstacle
    final int[] obstaclePositions;

    @JsonCreator
    public Map(
            @JsonProperty("width") int width,
            @JsonProperty("height") int height,
            @JsonProperty("worldTick") long worldTick,
            @JsonProperty("snakeInfos") SnakeInfo[] snakeInfos,
            @JsonProperty("foodPositions") int[] foodPositions,
            @JsonProperty("obstaclePositions") int[] obstaclePositions
    )
    {
        this.width = width;
        this.height = height;
        this.worldTick = worldTick;
        this.snakeInfos = snakeInfos;
        this.foodPositions = foodPositions;
        this.obstaclePositions = obstaclePositions;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public SnakeInfo[] getSnakeInfos() {
        return snakeInfos;
    }

    public long getWorldTick() {
        return worldTick;
    }

    public int[] getFoodPositions() {
        return foodPositions;
    }

    public int[] getObstaclePositions() {
        return obstaclePositions;
    }
}
