package se.cygni.snake.api.util;

import se.cygni.snake.api.model.GameSettings;

public class GameSettingsUtils {

    public static GameSettings trainingWorld() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(5)
                .withFoodEnabled(true)
                .withObstaclesEnabled(true)
                .build();
    }

    public static GameSettings eightPlayerWorld() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(8)
                .withFoodEnabled(true)
                .withObstaclesEnabled(false)
                .build();
    }

    public static GameSettings twelvePlayerWorld() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(12)
                .withFoodEnabled(true)
                .withObstaclesEnabled(false)
                .build();
    }

    public static GameSettings defaultTournament() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(15)
                .withFoodEnabled(true)
                .withObstaclesEnabled(true)
                .build();
    }
}
