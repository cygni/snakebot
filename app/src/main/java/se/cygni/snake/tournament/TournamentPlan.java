package se.cygni.snake.tournament;

import se.cygni.snake.game.GameFeatures;
import se.cygni.snake.game.PlayerManager;
import se.cygni.snake.player.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static se.cygni.snake.tournament.util.TournamentUtil.getMaxNoofPlayersPerGame;
import static se.cygni.snake.tournament.util.TournamentUtil.getNoofPlayersAdvancing;

public class TournamentPlan {

    private List<TournamentLevel> levels = new ArrayList<>();
    private final GameFeatures gameFeatures;
    private final PlayerManager playerManager;


    public TournamentPlan(
            GameFeatures gameFeatures,
            PlayerManager playerManager) {

        this.gameFeatures = gameFeatures;
        this.playerManager = playerManager;

        createPlan();
    }

    public TournamentLevel getLevelAt(int pos) {
        if (pos < 0 || pos >= levels.size()) {
            throw new RuntimeException("Idiot, you tried to get a level out of bounds.");
        }

        return levels.get(pos);
    }

    public List<TournamentLevel> getLevels() {
        return levels;
    }

    private void createPlan() {

        int maxPlayersPerGame = getMaxNoofPlayersPerGame(gameFeatures);

        int noofPlayersLeft = playerManager.size();
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
        return playerManager.toSet();
    }
}
