package se.cygni.snake.game;

import org.junit.Assert;
import org.junit.Test;

public class GameFeaturesTest {

    @Test
    public void testStartSnakeLengthIsMax10() throws Exception {
        GameFeatures gf = new GameFeatures();
        gf.setStartSnakeLength(22);
        gf.applyValidation();

        Assert.assertEquals(10, gf.getStartSnakeLength());
    }

    @Test
    public void testStartSnakeLengthIsMin1() throws Exception {
        GameFeatures gf = new GameFeatures();
        gf.setStartSnakeLength(-12);
        gf.applyValidation();

        Assert.assertEquals(1, gf.getStartSnakeLength());

        gf.setStartSnakeLength(0);
        gf.applyValidation();
        Assert.assertEquals(1, gf.getStartSnakeLength());
    }

    @Test
    public void testMaxNoofPlayersIsMax20() throws Exception {
        GameFeatures gf = new GameFeatures();
        gf.setMaxNoofPlayers(99);
        gf.applyValidation();

        Assert.assertEquals(20, gf.getMaxNoofPlayers());
    }

    @Test
    public void testMaxNoofPlayersIsMin2() throws Exception {
        GameFeatures gf = new GameFeatures();
        gf.setMaxNoofPlayers(-2);
        gf.applyValidation();

        Assert.assertEquals(2, gf.getMaxNoofPlayers());

        gf.setMaxNoofPlayers(0);
        gf.applyValidation();

        Assert.assertEquals(2, gf.getMaxNoofPlayers());
    }

    @Test
    public void testSpontaneousGrowthEveryNWorldTickIsMin2() throws Exception {
        GameFeatures gf = new GameFeatures();
        gf.setSpontaneousGrowthEveryNWorldTick(-2);
        gf.applyValidation();

        Assert.assertEquals(2, gf.getSpontaneousGrowthEveryNWorldTick());

        gf.setSpontaneousGrowthEveryNWorldTick(0);
        gf.applyValidation();

        Assert.assertEquals(2, gf.getSpontaneousGrowthEveryNWorldTick());
    }

    @Test
    public void testStartObstaclesIsPositive() throws Exception {
        GameFeatures gf = new GameFeatures();
        gf.setStartObstacles(-2);
        gf.applyValidation();

        Assert.assertEquals(0, gf.getStartObstacles());

        gf.setStartObstacles(0);
        gf.applyValidation();

        Assert.assertEquals(0, gf.getStartObstacles());

        gf.setStartObstacles(7);
        gf.applyValidation();

        Assert.assertEquals(7, gf.getStartObstacles());
    }

    @Test
    public void testStartFoodIsPositive() throws Exception {
        GameFeatures gf = new GameFeatures();
        gf.setStartFood(-2);
        gf.applyValidation();

        Assert.assertEquals(0, gf.getStartFood());

        gf.setStartFood(0);
        gf.applyValidation();

        Assert.assertEquals(0, gf.getStartFood());

        gf.setStartFood(7);
        gf.applyValidation();

        Assert.assertEquals(7, gf.getStartFood());
    }
}