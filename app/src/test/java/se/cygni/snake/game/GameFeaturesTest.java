package se.cygni.snake.game;

import org.junit.Assert;
import org.junit.Test;

public class GameFeaturesTest {

    @Test
    public void testDivisibleBy25() throws Exception {
        GameFeatures gf = new GameFeatures();

        gf.width = 74;
        gf.height = 34;

        gf.applyValidation();

        Assert.assertEquals(50, gf.width);
        Assert.assertEquals(25, gf.height);
    }

    @Test
    public void testMinSize25() throws Exception {
        GameFeatures gf = new GameFeatures();

        gf.width = 1;
        gf.height = -4;

        gf.applyValidation();

        Assert.assertEquals(25, gf.width);
        Assert.assertEquals(25, gf.height);
    }

    @Test
    public void testMaxSize100() throws Exception {
        GameFeatures gf = new GameFeatures();

        gf.width = 100;
        gf.height = 175;

        gf.applyValidation();

        Assert.assertEquals(100, gf.width);
        Assert.assertEquals(100, gf.height);
    }
}