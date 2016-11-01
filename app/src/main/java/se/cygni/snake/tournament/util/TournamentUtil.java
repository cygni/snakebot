package se.cygni.snake.tournament.util;

import se.cygni.snake.player.IPlayer;

import java.util.*;

public class TournamentUtil {

    /**
     * Given the total number of players and the available number of games this
     * method distributes the number of players per game as evenly as possible.
     *
     * @param noofPlayers
     * @param noofGames
     * @return An integer array with the number of players per game
     */
    public static int[] getNumberOfPlayersPerGame(int noofPlayers, int noofGames) {
        if (noofPlayers <= 0) {
            return new int[0];
        }

        int[] playersPerGame = new int[noofGames];

        for (int i = 0; i < noofPlayers; i++) {
            playersPerGame[i%noofGames] += 1;
        }
        return playersPerGame;
    }

    /**
     * Calculates the number of levels needed to determine a winner in a final game
     * given the number of players and max number of players per game. The calculation
     * is based on the fact that the number of games in each level is always 2^levelIndex.
     *
     * @param noofPlayers
     * @param maxNoofPlayersPerGame
     * @return The numbers of levels needed to establish a winner.
     */
    public static int getNoofLevels(int noofPlayers, int maxNoofPlayersPerGame) {
        if (noofPlayers < 1) {
            return 0;
        }

        if (noofPlayers <= maxNoofPlayersPerGame) {
            return 1;
        }

        int level = 0;
        double noofMatches =(double) noofPlayers / (double) maxNoofPlayersPerGame;

        // Find the *first* exponent of 2 that enables more than noofMatches
        while (true) {
            if (Math.pow(2, level) >= noofMatches)
                return level + 1;

            level++;
        }
    }

    /**
     * Calculates the number of games needed per level. The number
     * of games per level are always 2^levelIndex. I.e. the number of games
     * in level 3: 2^3 = 8
     *
     * @param noofLevels
     * @return An array of ints with the number of games per level
     */
    public static int[] getGameDistribution(int noofLevels) {
        if (noofLevels < 1) {
            return new int[0];
        }

        int[] gameDistribution = new int[noofLevels];

        int c = 0;
        for (int i = noofLevels-1; i>= 0; i--) {
            gameDistribution[c++] = (int)Math.pow(2, i);
        }

        return gameDistribution;
    }

    public static Set<IPlayer> getRandomPlayers(Set<IPlayer> players, int noofPlayers) {
        Set<IPlayer> randomPlayers = new HashSet<>();
        List<IPlayer> startPlayers = new ArrayList<>(players);
        Collections.shuffle(startPlayers);
        int actualNoofPlayers = Math.min(noofPlayers, startPlayers.size());
        for (int i = 0; i < actualNoofPlayers; i++) {
            randomPlayers.add(startPlayers.get(i));
        }
        return randomPlayers;
    }
}
