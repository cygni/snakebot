package se.cygni.snake.apiconversion;

import org.junit.Test;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.game.GameFeatures;

import static org.junit.Assert.assertEquals;

public class GameSettingsConverterTest {

    @Test
    public void testToGameSettings() throws Exception {
        GameFeatures gameFeatures = new GameFeatures();
        gameFeatures.setTrainingGame(false);
        gameFeatures.setPointsSuicide(-100);

        GameSettings gameSettings = GameSettingsConverter.toGameSettings(gameFeatures);

        assertEquals(false, gameSettings.isTrainingGame());
        assertEquals(-100, gameSettings.getPointsSuicide());
    }

    @Test
    public void testToGameFeatures() throws Exception {
        GameSettings gs = new GameSettings.GameSettingsBuilder()
                .withWidth(33)
                .withHeight(55)
                .withMaxNoofPlayers(3)
                .build();

        GameFeatures gf = GameSettingsConverter.toGameFeatures(gs);

        assertEquals(25, gf.width);
        assertEquals(50, gf.height);
        assertEquals(3, gf.maxNoofPlayers);
    }
}