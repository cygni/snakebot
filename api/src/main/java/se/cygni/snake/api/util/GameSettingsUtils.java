package se.cygni.snake.api.util;

import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.model.WorldSize;

public class GameSettingsUtils {

    public static GameSettings trainingWorld() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(1)
                .withWidth(WorldSize.SMALL)
                .withHeight(WorldSize.SMALL)
                .withFoodEnabled(true)
                .withObstaclesEnabled(false)
                .build();
    }

    public static GameSettings eightPlayerWorld() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(8)
                .withWidth(WorldSize.MEDIUM)
                .withHeight(WorldSize.MEDIUM)
                .withFoodEnabled(true)
                .withObstaclesEnabled(false)
                .build();
    }

    public static GameSettings twelvePlayerWorld() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(12)
                .withWidth(WorldSize.LARGE)
                .withHeight(WorldSize.LARGE)
                .withFoodEnabled(true)
                .withObstaclesEnabled(false)
                .build();
    }

    public static GameSettings defaultTournament() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(15)
                .withWidth(WorldSize.XLARGE)
                .withHeight(WorldSize.XLARGE)
                .withFoodEnabled(true)
                .withObstaclesEnabled(true)
                .build();
    }
}
