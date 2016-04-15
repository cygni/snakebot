package se.cygni.snake.tournament;

import se.cygni.snake.player.IPlayer;

import java.util.Set;

public class TournamentPlannedGame {
    private int expectedNoofPlayers;
    private Set<IPlayer> players;

    public int getExpectedNoofPlayers() {
        return expectedNoofPlayers;
    }

    public void setExpectedNoofPlayers(int expectedNoofPlayers) {
        this.expectedNoofPlayers = expectedNoofPlayers;
    }

    public Set<IPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(Set<IPlayer> players) {
        this.players = players;
    }
}
