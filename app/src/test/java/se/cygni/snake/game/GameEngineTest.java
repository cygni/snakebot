package se.cygni.snake.game;

import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

public class GameEngineTest {

    private GameEngine gameEngine;
    private Game game;

    @Before
    public void setup() {
        GameFeatures gameFeatures = new GameFeatures();
        gameFeatures.width = 15;
        gameFeatures.height = 15;
        gameFeatures.timeInMsPerTick = 1000;
        gameFeatures.maxNoofPlayers = 25;
        gameFeatures.spontaneousGrowthEveryNWorldTick = 2;
        gameFeatures.trainingGame = true;

        GameManager gameManager = new GameManager(new EventBus());
        game = gameManager.createGame(gameFeatures);

        gameEngine = game.getGameEngine();
    }

    @Test
    public void testSimpleGame() {
        game.startGame();

        do {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        } while (game.getGameEngine().isGameRunning());
    }


}
