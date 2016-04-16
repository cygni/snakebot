package se.cygni.snake.apiconversion;

import se.cygni.snake.eventapi.model.ActiveGamePlayer;
import se.cygni.snake.eventapi.model.TournamentGame;
import se.cygni.snake.eventapi.model.TournamentGamePlan;
import se.cygni.snake.eventapi.model.TournamentLevel;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.tournament.TournamentPlan;
import se.cygni.snake.tournament.TournamentPlannedGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TournamentPlanConverter {

    public static TournamentGamePlan getTournamentPlan(
            TournamentPlan plan,
            String tournamentName,
            String tournamentId) {

        TournamentGamePlan tgp = new TournamentGamePlan(plan.getLevels().size(), tournamentName, tournamentId);
        tgp.setPlayers(getPlayers(plan.getPlayers()));

        tgp.setTournamentLevels(getLevels(plan.getLevels()));
        return tgp;
    }

    public static List<ActiveGamePlayer> getPlayers(Set<IPlayer> players) {
        List<ActiveGamePlayer> activePlayers = new ArrayList<>();
        players.stream().forEach(player -> {
            activePlayers.add(new ActiveGamePlayer(player.getName(), player.getPlayerId()));
        });
        return activePlayers;
    }

    public static List<TournamentLevel> getLevels(List<se.cygni.snake.tournament.TournamentLevel> levels) {
        List<TournamentLevel> tlevels = new ArrayList<>();
        levels.stream().forEach(level -> {
            TournamentLevel tlevel = new TournamentLevel(level.getLevelIndex(), level.getExpectedNoofPlayers());
            tlevel.setTournamentGames(getTournamentGames(level.getPlannedGames()));
            tlevels.add(tlevel);
        });
        return tlevels;
    }

    private static List<TournamentGame> getTournamentGames(List<TournamentPlannedGame> plannedGames) {
        List<TournamentGame> games = new ArrayList<>();
        plannedGames.stream().forEach(tgp -> {
            TournamentGame game = new TournamentGame();
            game.setExpectedNoofPlayers(tgp.getExpectedNoofPlayers());
            games.add(game);
        });
        return games;
    }
}
