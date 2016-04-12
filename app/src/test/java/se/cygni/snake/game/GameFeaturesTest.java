package se.cygni.snake.game;

import org.junit.Assert;
import org.junit.Test;

public class GameFeaturesTest {

    @Test
    public void testDivisibleBy25() throws Exception {
        GameFeatures gf = new GameFeatures();

        gf.setWidth(74);
        gf.setHeight(34);

        gf.applyValidation();

        Assert.assertEquals(50, gf.getWidth());
        Assert.assertEquals(25, gf.getHeight());
    }

    @Test
    public void testMinSize25() throws Exception {
        GameFeatures gf = new GameFeatures();

        gf.setWidth(1);
        gf.setHeight(-4);

        gf.applyValidation();

        Assert.assertEquals(25, gf.getWidth());
        Assert.assertEquals(25, gf.getHeight());
    }

    @Test
    public void testMaxSize100() throws Exception {
        GameFeatures gf = new GameFeatures();

        gf.setWidth(100);
        gf.setHeight(175);

        gf.applyValidation();

        Assert.assertEquals(100, gf.getWidth());
        Assert.assertEquals(100, gf.getHeight());
    }
}