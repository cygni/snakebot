package se.cygni.snake.game;

import org.junit.Test;
import se.cygni.snake.api.model.PointReason;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.player.RemotePlayer;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameResultTest {



    @Test
    public void testGetWinnerSimple() throws Exception {
        GameResult result = new GameResult();

        RemotePlayer p1 = createPlayer("p1", true, 15);
        RemotePlayer p2 = createPlayer("p2", false, 3);
        RemotePlayer p3 = createPlayer("p3", true, 8);
        RemotePlayer p4 = createPlayer("p4", true, 4);


        result.addResult(p1);
        result.addResult(p2);
        result.addResult(p3);
        result.addResult(p4);

        assertEquals(p1, result.getWinner());
    }

    @Test
    public void testGetWinnerAllDead() throws Exception {
        GameResult result = new GameResult();

        RemotePlayer p1 = createPlayer("p1", false, 15);
        RemotePlayer p2 = createPlayer("p2", false, 3);
        RemotePlayer p3 = createPlayer("p3", false, 25);
        RemotePlayer p4 = createPlayer("p4", false, 2);


        result.addResult(p1);
        result.addResult(p2);
        result.addResult(p3);
        result.addResult(p4);

        assertEquals(p3, result.getWinner());
    }

    @Test
    public void testWinnerEqualPoints() throws Exception {
        GameResult result = new GameResult();

        RemotePlayer p1 = createPlayer("p1", true, 15);
        RemotePlayer p2 = createPlayer("p2", false, 99);
        RemotePlayer p3 = createPlayer("p3", true, 15);
        RemotePlayer p4 = createPlayer("p4", false, 2);

        when(p3.getPointsBy(PointReason.GROWTH)).thenReturn(6);
        when(p1.getPointsBy(PointReason.GROWTH)).thenReturn(3);

        result.addResult(p1);
        result.addResult(p2);
        result.addResult(p3);
        result.addResult(p4);

        assertEquals(p3, result.getWinner());
    }

    @Test
    public void testGetSortedResult() throws Exception {
        GameResult result = new GameResult();

        RemotePlayer p1 = createPlayer("p1", true, 15);
        RemotePlayer p2 = createPlayer("p2", false, 99);
        RemotePlayer p3 = createPlayer("p3", true, 15);
        RemotePlayer p4 = createPlayer("p4", false, 2);

        when(p3.getPointsBy(PointReason.GROWTH)).thenReturn(6);
        when(p1.getPointsBy(PointReason.GROWTH)).thenReturn(6);

        when(p3.getPointsBy(PointReason.FOOD)).thenReturn(2);
        when(p1.getPointsBy(PointReason.FOOD)).thenReturn(3);

        result.addResult(p1);
        result.addResult(p2);
        result.addResult(p3);
        result.addResult(p4);

        List<IPlayer> resultList = result.getSortedResult();

        assertEquals(p1, resultList.get(0));
        assertEquals(p3, resultList.get(1));
        assertEquals(p2, resultList.get(2));
        assertEquals(p4, resultList.get(3));
    }


    private RemotePlayer createPlayer(String name, boolean isAlive, int totalPoints) {
        RemotePlayer mockPlayer = mock(RemotePlayer.class);
        when(mockPlayer.getName()).thenReturn(name);
        when(mockPlayer.getPlayerId()).thenReturn("id-" + name);
        when(mockPlayer.isAlive()).thenReturn(isAlive);
        when(mockPlayer.getTotalPoints()).thenReturn(totalPoints);

        return mockPlayer;
    }
}