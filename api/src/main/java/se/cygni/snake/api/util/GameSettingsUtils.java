package se.cygni.snake.api.util;

import se.cygni.snake.api.model.GameSettings;

public class GameSettingsUtils {

    public static GameSettings onePlayerSmallWorld() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(1)
                .withWidth(25)
                .withHeight(25)
                .withFoodEnabled(true)
                .withObstaclesEnabled(false)
                .build();
    }

    public static GameSettings fivePlayersMediumWorld() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(5)
                .withWidth(50)
                .withHeight(50)
                .withFoodEnabled(true)
                .withObstaclesEnabled(false)
                .build();
    }

    public static GameSettings defaultTournament() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(10)
                .withWidth(100)
                .withHeight(100)
                .withFoodEnabled(true)
                .withObstaclesEnabled(true)
                .build();
    }
}
