package se.cygni.snake.tournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.game.GameResult;
import se.cygni.snake.player.HistoricalPlayer;
import se.cygni.snake.player.IPlayer;
import se.cygni.snake.tournament.util.TournamentUtil;

import java.util.*;

public class TournamentLevel {

    private static final Logger log = LoggerFactory.getLogger(TournamentLevel.class);

    private final int levelIndex;
    private final int expectedNoofPlayers;
    private final int maxNoofPlayersPerGame;
    private final int noofGamesInLevel;
    private Set<IPlayer> players;
    private List<TournamentPlannedGame> plannedGames;

    public TournamentLevel(int levelIndex, int noofGamesInLevel, int expectedNoofPlayers, int maxNoofPlayersPerGame) {
        this.levelIndex = levelIndex;
        this.noofGamesInLevel = noofGamesInLevel;
        this.expectedNoofPlayers = expectedNoofPlayers;
        this.maxNoofPlayersPerGame = maxNoofPlayersPerGame;
        planGames();
    }

    public Set<IPlayer> getPlayersAdvancing() {

        Set<IPlayer> playersAdvancing = new HashSet<>();

        // Store players in GameResult
        for (TournamentPlannedGame game : plannedGames) {
            game.createHistoricalGameResult();
        }

        // Alive winning players always proceed
        for (TournamentPlannedGame game : plannedGames) {
            List<IPlayer> gameResult = game.getGame().getGameResult().getSortedResult();
            if (gameResult.size() > 0) {
                IPlayer player = gameResult.get(0);
                if (player.isConnected()) {
                    playersAdvancing.add(player);

                    HistoricalPlayer hPlayer = game.getHistoricalPlayer(player.getPlayerId());
                    hPlayer.setMovedUpInTournament(true);
                    hPlayer.setWinner(true);
                }
            }
        }

        int noofPlayersNeedForNextLevel = (noofGamesInLevel / 2) * maxNoofPlayersPerGame;
        int diff = noofPlayersNeedForNextLevel - playersAdvancing.size();

        // How many players can we fairly add from all games?
        int toFairlyAddPerGame = diff / noofGamesInLevel;
        for (TournamentPlannedGame game : plannedGames) {
            List<IPlayer> gameResult = game.getGame().getGameResult().getSortedResult();

            int added = 0;
            int i = 0;
            boolean done = added == diff;
            while (!done) {
                IPlayer player = gameResult.get(i);
                if (player.isConnected() && !playersAdvancing.contains(player)) {
                    playersAdvancing.add(player);

                    HistoricalPlayer hPlayer = game.getHistoricalPlayer(player.getPlayerId());
                    hPlayer.setMovedUpInTournament(true);

                    added++;
                }

                done = added == toFairlyAddPerGame || i >= gameResult.size();
                i++;
            }
        }

        // Okay, what's the diff now?
        diff = noofPlayersNeedForNextLevel - playersAdvancing.size();

        Map<String, TournamentPlannedGame> playerToPlannedGameMap = new HashMap<>();
        if (diff > 0) {
            GameResult totalGameResult = new GameResult();

            for (TournamentPlannedGame game : plannedGames) {
                List<IPlayer> gameResult = game.getGame().getGameResult().getSortedResult();

                for (IPlayer player : gameResult) {
                    if (player.isConnected() && !playersAdvancing.contains(player)) {
                        totalGameResult.addResult(player);
                        playerToPlannedGameMap.put(player.getPlayerId(), game);
                    }
                }
            }

            // Add the remaining players with highest score!
            List<IPlayer> totalRestResult = totalGameResult.getSortedResult();
            for (int i=0; i < diff; i++) {
                IPlayer player = totalRestResult.get(i);
                playersAdvancing.add(player);

                // setMovedUpInTournament to HistoricalPlayer
                playerToPlannedGameMap.get(player.getPlayerId()).getHistoricalPlayer(player.getPlayerId()).setMovedUpInTournament(true);
            }
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

        int[] playerDistribution = TournamentUtil.getNumberOfPlayersPerGame(expectedNoofPlayers, noofGamesInLevel);
        for (int noofPlayers : playerDistribution) {
            TournamentPlannedGame tpg = new TournamentPlannedGame();
            tpg.setExpectedNoofPlayers(noofPlayers);
            plannedGames.add(tpg);
        }

    }
}
