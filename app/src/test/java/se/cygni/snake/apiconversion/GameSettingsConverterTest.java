package se.cygni.snake.apiconversion;

import org.junit.Test;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.model.WorldSize;
import se.cygni.snake.game.GameFeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameSettingsConverterTest {

    @Test
    public void testToGameSettings() throws Exception {
        GameFeatures gameFeatures = new GameFeatures();
        gameFeatures.setWidth(25);
        gameFeatures.setHeight(25);
        gameFeatures.setTrainingGame(false);
        gameFeatures.setPointsSuicide(-100);

        GameSettings gameSettings = GameSettingsConverter.toGameSettings(gameFeatures);

        assertEquals(25, gameSettings.getWidth().getSize());
        assertEquals(25, gameSettings.getHeight().getSize());
        assertEquals(false, gameSettings.isTrainingGame());
        assertEquals(-100, gameSettings.getPointsSuicide());
    }

    @Test
    public void testFromGameSettings() throws Exception {
        GameSettings gameSettings = new GameSettings();
        gameSettings.setHeight(WorldSize.SMALL);
        gameSettings.setWidth(WorldSize.SMALL);
        gameSettings.setTrainingGame(true);
        gameSettings.setPointsSuicide(100);

        GameFeatures gameFeatures = GameSettingsConverter.toGameFeatures(gameSettings);

        assertEquals(25, gameFeatures.getHeight());
        assertEquals(25, gameFeatures.getWidth());
        assertTrue(gameFeatures.isTrainingGame());
    }
}