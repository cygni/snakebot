package se.cygni.snake.tournament;

import se.cygni.snake.game.GameFeatures;
import se.cygni.snake.player.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static se.cygni.snake.tournament.util.TournamentUtil.*;

public class TournamentPlan {

    private List<TournamentLevel> levels = new ArrayList<>();
    private final GameFeatures gameFeatures;
    private final Set<IPlayer> players;


    public TournamentPlan(GameFeatures gameFeatures, Set<IPlayer> players) {
        this.gameFeatures = gameFeatures;
        this.players = players;

        createPlan();
    }

    public List<TournamentLevel> getLevels() {
        return levels;
    }

    private void createPlan() {

        int maxPlayersPerGame = getMaxNoofPlayersPerGame(gameFeatures);

        int noofPlayersLeft = players.size();
        boolean notSolved = noofPlayersLeft > maxPlayersPerGame;
        int index = 0;
        while (notSolved) {
            TournamentLevel level = new TournamentLevel(index, noofPlayersLeft, maxPlayersPerGame);
            levels.add(level);

            noofPlayersLeft = getNoofPlayersAdvancing(noofPlayersLeft, maxPlayersPerGame);

            notSolved = noofPlayersLeft > maxPlayersPerGame;
            index++;
        }
        TournamentLevel level = new TournamentLevel(index, noofPlayersLeft, maxPlayersPerGame);
        levels.add(level);
    }

    public GameFeatures getGameFeatures() {
        return gameFeatures;
    }

    public Set<IPlayer> getPlayers() {
        return players;
    }
}
