package se.cygni.snake.api.event.model;

import org.junit.Test;
import se.cygni.snake.api.model.WorldSize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Alan Tibbetts
 * @since 12/04/16
 *
 */
public class WorldSizeTest {

    @Test
    public void testSizesDivisibleBy25() {
        for (WorldSize worldSize : WorldSize.values()) {
            int remainder = worldSize.getSize() % 25;
            String message = String.format("WorldSize.%s has size: %s, should be divisible by 25", worldSize, worldSize.getSize());
            assertEquals(message, 0, remainder);
        }
    }

    @Test
    public void testGetForSize() {
        WorldSize worldSize = WorldSize.getForSize(75);
        assertTrue(WorldSize.LARGE == worldSize);
    }
}
