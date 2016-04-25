package se.cygni.snake.tournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.tournament.util.TournamentUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TournamentLevel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TournamentLevel.class);

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

    public Set<IPlayer> getPlayersAdvancing() {
        int diff = TournamentUtil.getNoofPlayersOut(maxNoofPlayersPerGame);
        Set<IPlayer> playersAdvancing = new HashSet<>();

        for (TournamentPlannedGame game : plannedGames) {
            List<IPlayer> gameResult = game.getGame().getGameResult().getSortedResult();
            int noofToAdvance;

            // At least one player should advance
            if (gameResult.size() + diff <= 0) {
                noofToAdvance = 1;
            } else {
                noofToAdvance = gameResult.size() + diff;
            }

            List<IPlayer> gameAdvancing = new ArrayList<>();
            boolean addedEnough = false;
            int added = 0;
            int index = 0;
            while (!addedEnough) {
                IPlayer player = gameResult.get(index);
                if (player.isConnected()) {
                    gameAdvancing.add(player);
                    added++;
                }
                index++;
                addedEnough = added == noofToAdvance || index >= gameResult.size();
            }
            LOGGER.info("Noof players in this game: {}, gameResult size: {}, advancing: {}",
                    game.getGame().getPlayerManager().size(),
                    gameResult.size(),
                    gameAdvancing.size());

            playersAdvancing.addAll(gameAdvancing);
        }

        for (IPlayer player : playersAdvancing) {
            player.reset();
            player.revive();
        }

        return playersAdvancing;
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

    public Set<IPlayer> getPlayers() {
        return players;
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
