package se.cygni.snake.eventapi.model;

import java.util.List;

public class TournamentGame {

    private String gameId;
    private int expectedNoofPlayers;
    private List<ActiveGamePlayer> players;

    public TournamentGame() {
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getExpectedNoofPlayers() {
        return expectedNoofPlayers;
    }

    public void setExpectedNoofPlayers(int expectedNoofPlayers) {
        this.expectedNoofPlayers = expectedNoofPlayers;
    }

    public List<ActiveGamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<ActiveGamePlayer> players) {
        this.players = players;
    }
}
