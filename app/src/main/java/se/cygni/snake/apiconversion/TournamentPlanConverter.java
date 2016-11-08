package se.cygni.snake.apiconversion;

import se.cygni.snake.eventapi.model.ActiveGamePlayer;
import se.cygni.snake.eventapi.model.TournamentGame;
import se.cygni.snake.eventapi.model.TournamentGamePlan;
import se.cygni.snake.eventapi.model.TournamentLevel;
import se.cygni.snake.player.HistoricalPlayer;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.tournament.TournamentPlan;
import se.cygni.snake.tournament.TournamentPlannedGame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static List<ActiveGamePlayer> getPlayers(Collection<IPlayer> players) {
        List<ActiveGamePlayer> activePlayers = new ArrayList<>();
        if (players == null) {
            return activePlayers;
        }

        players.forEach(player -> {
            activePlayers.add(getPlayer(player));
        });
        return activePlayers;
    }

    public static ActiveGamePlayer getPlayer(IPlayer player) {
        if (player instanceof HistoricalPlayer) {
            HistoricalPlayer historicalPlayer = (HistoricalPlayer)player;
            return new ActiveGamePlayer(historicalPlayer.getName(),
                    historicalPlayer.getPlayerId(),
                    historicalPlayer.getTotalPoints(),
                    historicalPlayer.isWinner(),
                    historicalPlayer.isMovedUpInTournament());
        } else {
            return new ActiveGamePlayer(player.getName(), player.getPlayerId(), player.getTotalPoints());
        }
    }

    public static List<TournamentLevel> getLevels(List<se.cygni.snake.tournament.TournamentLevel> levels) {
        List<TournamentLevel> tlevels = new ArrayList<>();
        levels.forEach(level -> {
            TournamentLevel tlevel = new TournamentLevel(level.getLevelIndex(), level.getExpectedNoofPlayers());
            tlevel.setPlayers(getPlayers(level.getPlayers()));
            tlevel.setTournamentGames(getTournamentGames(level.getPlannedGames()));
            tlevels.add(tlevel);
        });
        return tlevels;
    }

    private static List<TournamentGame> getTournamentGames(List<TournamentPlannedGame> plannedGames) {
        List<TournamentGame> games = new ArrayList<>();
        plannedGames.forEach(tgp -> {
            TournamentGame game = new TournamentGame();
            game.setExpectedNoofPlayers(tgp.getExpectedNoofPlayers());


            List<IPlayer> gameResultPlayers = tgp.getGameResult().getSortedResult();
            if (gameResultPlayers.size() > 0) {
                game.setPlayers(getPlayers(gameResultPlayers));
                game.setGamePlayed(true);
            } else {
                game.setPlayers(getPlayers(tgp.getPlayers()));
                game.setGamePlayed(false);
            }

            if (tgp.getGame() != null) {
                game.setGameId(tgp.getGame().getGameId());
            }
            games.add(game);
        });
        return games;
    }
}
