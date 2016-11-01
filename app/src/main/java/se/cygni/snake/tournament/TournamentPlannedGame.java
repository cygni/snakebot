package se.cygni.snake.tournament;

import se.cygni.snake.game.Game;
import se.cygni.snake.game.GameResult;
import se.cygni.snake.player.HistoricalPlayer;
import se.cygni.snake.player.IPlayer;

import java.util.HashSet;
import java.util.Set;

public class TournamentPlannedGame {

    private int expectedNoofPlayers;
    private Set<IPlayer> players;
    private Set<HistoricalPlayer> historicalPlayers;
    private GameResult gameResult = new GameResult();
    private Game game;

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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public HistoricalPlayer getHistoricalPlayer(String playerId) {
        return historicalPlayers.stream().filter(player -> player.getPlayerId().equals(playerId)).findFirst().get();
    }

    public void createHistoricalGameResult() {
        historicalPlayers = new HashSet<>();

        for (IPlayer player : game.getPlayerManager().toSet()) {
            HistoricalPlayer hPlayer = new HistoricalPlayer(player);
            gameResult.addResult(hPlayer);
            historicalPlayers.add(hPlayer);
        }
    }
}
