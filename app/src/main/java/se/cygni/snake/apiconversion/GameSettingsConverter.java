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
        GameFeatures gameFeatures = new GameFeatures();
        BeanUtils.copyProperties(gameSettings, gameFeatures);
        gameFeatures.applyValidation();
        return gameFeatures;
    }
}
