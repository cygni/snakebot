package se.cygni.snake.player;

import org.junit.Test;
import se.cygni.game.Player;
import se.cygni.snake.api.model.PointReason;

import static org.junit.Assert.assertEquals;

public class IPlayerComparatorTest {

    @Test
    public void testCompare() throws Exception {

        Player pp1 = new Player("t1");
        pp1.setPlayerId("t1");
        Player pp2 = new Player("t2");
        pp2.setPlayerId("t2");

        RemotePlayer p1 = new RemotePlayer(pp1, null);
        RemotePlayer p2 = new RemotePlayer(pp2, null);

        p1.addPoints(PointReason.FOOD, 1);
        p2.addPoints(PointReason.FOOD, 1);

        assertEquals("t1".compareTo("t2"), new IPlayerComparator().compare(p1, p2));

        p1.addPoints(PointReason.NIBBLE, 10);
        assertEquals(-1, new IPlayerComparator().compare(p1, p2));

        p2.addPoints(PointReason.NIBBLE, 10);
        assertEquals("t1".compareTo("t2"), new IPlayerComparator().compare(p1, p2));


        p1.addPoints(PointReason.GROWTH, 10);
        p2.addPoints(PointReason.NIBBLE, 10);
        assertEquals(-1, new IPlayerComparator().compare(p1, p2));

        p2.dead(32);
        assertEquals(-1, new IPlayerComparator().compare(p1, p2));
    }
}