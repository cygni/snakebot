package se.cygni.snake.tournament;

import se.cygni.snake.player.IPlayer;
import se.cygni.snake.tournament.util.TournamentUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TournamentLevel {

    private final int levelIndex;
    private final int expectedNoofPlayers;
    private final int maxNoofPlayersPerGame;
    private Set<IPlayer> players;
    private List<TournamentPlannedGame> plannedGames;

    public TournamentLevel(int levelIndex, int expectedNoofPlayers, int maxNoofPlayersPerGame) {
        this.levelIndex = levelIndex;
        this.expectedNoofPlayers = expectedNoofPlayers;
        this.maxNoofPlayersPerGame = maxNoofPlayersPerGame;
        planGames();
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public int getExpectedNoofPlayers() {
        return expectedNoofPlayers;
    }

    public void setPlayers(Set<IPlayer> players) {
        this.players = players;
    }

    public List<TournamentPlannedGame> getPlannedGames() {
        return plannedGames;
    }

    private void planGames() {
        plannedGames = new ArrayList<>();
        int noofGames = TournamentUtil.getNoofGamesForPlayers(expectedNoofPlayers, maxNoofPlayersPerGame);
        int[] playerDistribution = TournamentUtil.getPlayerDistribution(expectedNoofPlayers, noofGames);
        for (int noof : playerDistribution) {
            TournamentPlannedGame tpg = new TournamentPlannedGame();
            tpg.setExpectedNoofPlayers(noof);
            plannedGames.add(tpg);
        }
    }
}
