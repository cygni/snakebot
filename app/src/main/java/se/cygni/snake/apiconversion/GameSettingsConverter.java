package se.cygni.snake.apiconversion;

import org.springframework.beans.BeanUtils;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.game.GameFeatures;

public class GameSettingsConverter {

    public static GameSettings toGameSettings(GameFeatures gameFeatures) {
        GameSettings gameSettings = new GameSettings();
        gameFeatures.applyValidation();

        BeanUtils.copyProperties(gameFeatures, gameSettings);

        return gameSettings;
    }

    public static GameFeatures toGameFeatures(GameSettings gameSettings) {
        GameFeatures defaultGameFeatures = new GameFeatures();
        return toGameFeatures(gameSettings, defaultGameFeatures);
    }

    public static GameFeatures toGameFeatures(GameSettings gameSettings, GameFeatures prevGameFeatures) {
        // Used for checking if default values are set, to prevent overwriting of game features with default values.
        GameSettings defaultGameSettings = new GameSettings();
        // BeanUtils.copyProperties(gameSettings, gameFeatures); // Unused since it copies all fields.
        if (gameSettings.getMaxNoofPlayers() != defaultGameSettings.getMaxNoofPlayers()) {
            prevGameFeatures.setMaxNoofPlayers(gameSettings.getMaxNoofPlayers());
        }
        if (gameSettings.getStartSnakeLength() != defaultGameSettings.getStartSnakeLength()) {
            prevGameFeatures.setStartSnakeLength(gameSettings.getStartSnakeLength());
        }
        if (gameSettings.getTimeInMsPerTick() != defaultGameSettings.getTimeInMsPerTick()) {
            prevGameFeatures.setTimeInMsPerTick(gameSettings.getTimeInMsPerTick());
        }
        if (gameSettings.isObstaclesEnabled() != defaultGameSettings.isObstaclesEnabled()) {
            prevGameFeatures.setObstaclesEnabled(gameSettings.isObstaclesEnabled());
        }
        if (gameSettings.isFoodEnabled() != defaultGameSettings.isFoodEnabled()) {
            prevGameFeatures.setFoodEnabled(gameSettings.isFoodEnabled());
        }
        if (gameSettings.isHeadToTailConsumes() != defaultGameSettings.isHeadToTailConsumes()) {
            prevGameFeatures.setHeadToTailConsumes(gameSettings.isHeadToTailConsumes());
        }
        if (gameSettings.isTailConsumeGrows() != defaultGameSettings.isTailConsumeGrows()) {
            prevGameFeatures.setTailConsumeGrows(gameSettings.isTailConsumeGrows());
        }
        if (gameSettings.getAddFoodLikelihood() != defaultGameSettings.getAddFoodLikelihood()) {
            prevGameFeatures.setAddFoodLikelihood(gameSettings.getAddFoodLikelihood());
        }
        if (gameSettings.getRemoveFoodLikelihood() != defaultGameSettings.getRemoveFoodLikelihood()) {
            prevGameFeatures.setRemoveFoodLikelihood(gameSettings.getRemoveFoodLikelihood());
        }
        if (gameSettings.getSpontaneousGrowthEveryNWorldTick() != defaultGameSettings.getSpontaneousGrowthEveryNWorldTick()) {
            prevGameFeatures.setSpontaneousGrowthEveryNWorldTick(gameSettings.getSpontaneousGrowthEveryNWorldTick());
        }
        if (gameSettings.isTrainingGame() != defaultGameSettings.isTrainingGame()) {
            prevGameFeatures.setTrainingGame(gameSettings.isTrainingGame());
        }
        if (gameSettings.getPointsPerLength() != defaultGameSettings.getPointsPerLength()) {
            prevGameFeatures.setPointsPerLength(gameSettings.getPointsPerLength());
        }
        if (gameSettings.getPointsPerFood() != defaultGameSettings.getPointsPerFood()) {
            prevGameFeatures.setPointsPerFood(gameSettings.getPointsPerFood());
        }
        if (gameSettings.getPointsPerNibble() != defaultGameSettings.getPointsPerNibble()) {
            prevGameFeatures.setPointsPerNibble(gameSettings.getPointsPerNibble());
        }
        if (gameSettings.getNoofRoundsTailProtectedAfterNibble() != defaultGameSettings.getNoofRoundsTailProtectedAfterNibble()) {
            prevGameFeatures.setNoofRoundsTailProtectedAfterNibble(gameSettings.getNoofRoundsTailProtectedAfterNibble());
        }
        if (gameSettings.getStartFood() != defaultGameSettings.getStartFood()) {
            prevGameFeatures.setStartFood(gameSettings.getStartFood());
        }
        if (gameSettings.getStartObstacles() != defaultGameSettings.getStartObstacles()) {
            prevGameFeatures.setStartObstacles(gameSettings.getStartObstacles());
        }
        
        prevGameFeatures.applyValidation();
        // System.out.println("################ TOprevGameFeatures #####################");
        // System.out.println("gameSettings: " + gameSettings.toString());
        // System.out.println("prevGameFeatures: " + prevGameFeatures.toString());
        return prevGameFeatures;
    }
}
