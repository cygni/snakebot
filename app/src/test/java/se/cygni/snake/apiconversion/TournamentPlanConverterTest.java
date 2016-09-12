package se.cygni.snake.apiconversion;

import org.junit.Test;
import se.cygni.snake.eventapi.ApiMessageParser;
import se.cygni.snake.eventapi.model.TournamentGamePlan;
import se.cygni.snake.game.GameFeatures;
import se.cygni.snake.game.PlayerManager;
import se.cygni.snake.game.TournamentPlanTest;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.tournament.TournamentPlan;

import java.util.Set;

public class TournamentPlanConverterTest {

    @Test
    public void testGetTournamentPlan() throws Exception {
        GameFeatures gf = new GameFeatures();

        Set<IPlayer> players = TournamentPlanTest.getPlayers(20);
        PlayerManager playerManager = new PlayerManager();
        playerManager.addAll(players);

        TournamentPlan tp = new TournamentPlan(gf, playerManager);


        TournamentGamePlan tgp = TournamentPlanConverter.getTournamentPlan(tp, "test-tournament", "1234");
        System.out.println(ApiMessageParser.encodeMessage(tgp));
    }
}