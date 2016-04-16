package se.cygni.snake.game;

import org.junit.Test;
import se.cygni.game.Player;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.player.RemotePlayer;
import se.cygni.snake.tournament.TournamentLevel;
import se.cygni.snake.tournament.TournamentPlan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TournamentPlanTest {

    @Test
    public void testGetLevelsSmall() throws Exception {
        GameFeatures gf = new GameFeatures();
        gf.setHeight(25);
        gf.setWidth(25);

        Set<IPlayer> players = getPlayers(20);

        TournamentPlan tp = new TournamentPlan(gf, players);

        List<TournamentLevel> levels = tp.getLevels();
        assertEquals(6, levels.size());
        assertEquals(20, levels.get(0).getExpectedNoofPlayers());
        assertEquals(16, levels.get(1).getExpectedNoofPlayers());
        assertEquals(12, levels.get(2).getExpectedNoofPlayers());
        assertEquals(9, levels.get(3).getExpectedNoofPlayers());
        assertEquals(7, levels.get(4).getExpectedNoofPlayers());
        assertEquals(5, levels.get(5).getExpectedNoofPlayers());
    }

    @Test
    public void testGetLevelsXLarge() throws Exception {
        GameFeatures gf = new GameFeatures();
        gf.setHeight(100);
        gf.setWidth(100);

        Set<IPlayer> players = getPlayers(30);

        TournamentPlan tp = new TournamentPlan(gf, players);

        List<TournamentLevel> levels = tp.getLevels();
        assertEquals(2, levels.size());
        assertEquals(30, levels.get(0).getExpectedNoofPlayers());
        assertEquals(16, levels.get(1).getExpectedNoofPlayers());
    }

    public static Set<IPlayer> getPlayers(int noof) {
        Set<IPlayer> players = new HashSet<>();
        for (int i = 0; i < noof; i++) {
            Player p = new Player("testplayer_" + i);
            p.setPlayerId(UUID.randomUUID().toString());
            players.add(new RemotePlayer(p, null));
        }
        return players;
    }
}