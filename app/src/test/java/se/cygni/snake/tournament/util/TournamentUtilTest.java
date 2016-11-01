package se.cygni.snake.tournament.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TournamentUtilTest {

    @Test
    public void testGetPlayerToGameDistribution() throws Exception {

        assertArrayEquals(
                new int[] {3, 3, 2},
                TournamentUtil.getNumberOfPlayersPerGame(8, 3));

        assertArrayEquals(
                new int[] {4, 3, 3, 3},
                TournamentUtil.getNumberOfPlayersPerGame(13, 4));
    }

    @Test
    public void testGetPlayerToGameDistributionEven() throws Exception {

        assertArrayEquals(
                new int[] {6, 6},
                TournamentUtil.getNumberOfPlayersPerGame(12, 2));
    }

    @Test
    public void testGetPlayerToGameDistributionUnevenOne() throws Exception {

        assertArrayEquals(
                new int[] {5, 4, 4, 4},
                TournamentUtil.getNumberOfPlayersPerGame(17, 4));
    }

    @Test
    public void testGetPlayerToGameDistributionZeroPlayers() throws Exception {

        assertArrayEquals(
                new int[0],
                TournamentUtil.getNumberOfPlayersPerGame(0, 3));
    }

    @Test
    public void testGetNoofLevelsRoundsUp() throws Exception {

        assertEquals(
                5,
                TournamentUtil.getNoofLevels(100, 10));

        assertEquals(
                1,
                TournamentUtil.getNoofLevels(4, 5));

        assertEquals(
                3,
                TournamentUtil.getNoofLevels(13, 5));

        assertEquals(
                2,
                TournamentUtil.getNoofLevels(8, 5));

        assertEquals(
                2,
                TournamentUtil.getNoofLevels(20, 10));
    }

    @Test
    public void testGetNoofLevelsConditionalPlayersLessThanOrEqualMaxPlayers() throws Exception {

        assertEquals(
                1,
                TournamentUtil.getNoofLevels(8, 10));

        assertEquals(
                1,
                TournamentUtil.getNoofLevels(9, 10));

        assertEquals(
                1,
                TournamentUtil.getNoofLevels(10, 10));
    }

    @Test
    public void testGetNoofLevelsConditionalPlayersLessThanOne() throws Exception {

        assertEquals(
                1,
                TournamentUtil.getNoofLevels(1, 10));

        assertEquals(
                0,
                TournamentUtil.getNoofLevels(0, 10));

        assertEquals(
                0,
                TournamentUtil.getNoofLevels(-1, 10));
    }

    @Test
    public void testGetGameDistribution() throws Exception {

        assertArrayEquals(
                new int[] {8, 4, 2, 1},
                TournamentUtil.getGameDistribution(4));
    }

    @Test
    public void testGetGameDistributionOneLevel() throws Exception {

        assertArrayEquals(
                new int[] {1},
                TournamentUtil.getGameDistribution(1));
    }

    @Test
    public void testGetGameDistributionConditionalLevel() throws Exception {

        assertArrayEquals(
                new int[0],
                TournamentUtil.getGameDistribution(-1));

        assertArrayEquals(
                new int[0],
                TournamentUtil.getGameDistribution(0));

        assertArrayEquals(
                new int[] {1},
                TournamentUtil.getGameDistribution(1));


        assertArrayEquals(
                new int[] {4, 2, 1},
                TournamentUtil.getGameDistribution(3));
    }
}