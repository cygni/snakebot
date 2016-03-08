package se.cygni.snake.apiconversion;

import org.springframework.beans.BeanUtils;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.game.GameFeatures;

public class GameSettingsConverter {

    public static GameSettings toGameSettings(GameFeatures gameFeatures) {
        GameSettings.GameSettingsBuilder builder = new GameSettings.GameSettingsBuilder();

        return builder
                .withMaxNoofPlayers(gameFeatures.maxNoofPlayers)
                .withHeight(gameFeatures.height)
                .withFoodEnabled(gameFeatures.foodEnabled)
                .withWidth(gameFeatures.width)
                .withObstaclesEnabled(gameFeatures.obstaclesEnabled)
                .withEdgeWrapsAround(gameFeatures.edgeWrapsAround)
                .withHeadToTailConsumes(gameFeatures.headToTailConsumes)
                .withStartSnakeLength(gameFeatures.startSnakeLength)
                .withTailConsumeGrows(gameFeatures.tailConsumeGrows)
                .withTimeInMsPerTick(gameFeatures.timeInMsPerTick)
                .build();
    }

    public static GameFeatures toGameFeatures(GameSettings gameSettings) {
        GameFeatures gameFeatures = new GameFeatures();
        BeanUtils.copyProperties(gameSettings, gameFeatures);
        gameFeatures.applyValidation();
        return gameFeatures;
    }
}
