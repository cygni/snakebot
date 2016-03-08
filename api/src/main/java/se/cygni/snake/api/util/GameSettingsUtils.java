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
                .withWidth(125)
                .withHeight(125)
                .withFoodEnabled(true)
                .withObstaclesEnabled(false)
                .build();
    }

    public static GameSettings defaultTournament() {
        return new GameSettings.GameSettingsBuilder()
                .withMaxNoofPlayers(10)
                .withWidth(500)
                .withHeight(500)
                .withFoodEnabled(true)
                .withObstaclesEnabled(true)
                .build();
    }
}
