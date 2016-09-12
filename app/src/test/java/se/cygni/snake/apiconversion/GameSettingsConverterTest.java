package se.cygni.snake.apiconversion;

import org.junit.Test;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.game.GameFeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameSettingsConverterTest {

    @Test
    public void testToGameSettings() throws Exception {
        GameFeatures gameFeatures = new GameFeatures();
        gameFeatures.setTrainingGame(false);

        GameSettings gameSettings = GameSettingsConverter.toGameSettings(gameFeatures);

        assertEquals(false, gameSettings.isTrainingGame());
    }

    @Test
    public void testFromGameSettings() throws Exception {
        GameSettings gameSettings = new GameSettings();
        gameSettings.setTrainingGame(true);
        GameFeatures gameFeatures = GameSettingsConverter.toGameFeatures(gameSettings);

        assertTrue(gameFeatures.isTrainingGame());
    }
}