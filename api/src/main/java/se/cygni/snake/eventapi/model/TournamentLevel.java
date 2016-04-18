package se.cygni.snake.eventapi.model;

import java.util.List;

public class TournamentLevel {

    private final int level;
    private final int expectedNoofPlayers;
    private List<ActiveGamePlayer> players;
    private List<TournamentGame> tournamentGames;

    public TournamentLevel(int level, int expectedNoofPlayers) {
        this.level = level;
        this.expectedNoofPlayers = expectedNoofPlayers;
    }

    public int getLevel() {
        return level;
    }

    public int getExpectedNoofPlayers() {
        return expectedNoofPlayers;
    }

    public List<TournamentGame> getTournamentGames() {
        return tournamentGames;
    }

    public void setTournamentGames(List<TournamentGame> tournamentGames) {
        this.tournamentGames = tournamentGames;
    }

    public List<ActiveGamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<ActiveGamePlayer> players) {
        this.players = players;
    }
}
