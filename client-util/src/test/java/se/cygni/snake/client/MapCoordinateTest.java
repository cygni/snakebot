package se.cygni.snake.client;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapCoordinateTest {

    @Test
    public void testTranslateBy() throws Exception {
        MapCoordinate start = new MapCoordinate(2,1);
        MapCoordinate end = start.translateBy(3, 3);

        assertEquals(new MapCoordinate(5, 4), end);
    }

    @Test
    public void testGetManhattanDistanceTo() throws Exception {
        MapCoordinate start = new MapCoordinate(2,1);
        MapCoordinate end = new MapCoordinate(6,7);
        assertEquals(10, start.getManhattanDistanceTo(end));
    }

    @Test
    public void testGetManhattanDistanceIsZero() throws Exception {
        MapCoordinate start = new MapCoordinate(2,1);
        assertEquals(0, start.getManhattanDistanceTo(start));
    }
}