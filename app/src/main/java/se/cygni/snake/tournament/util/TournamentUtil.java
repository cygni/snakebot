package se.cygni.snake.tournament.util;

import se.cygni.snake.game.GameFeatures;
import se.cygni.snake.player.IPlayer;

import java.util.*;

public class TournamentUtil {

    private static int PLAYERS_SMALL = 5;
    private static int PLAYERS_MEDIUM = 8;
    private static int PLAYERS_LARGE = 13;
    private static int PLAYERS_XLARGE = 17;


    public static Set<IPlayer> getRandomPlayers(Set<IPlayer> players, int noofPlayers) {
        Set<IPlayer> randomPlayers = new HashSet<>();
        List<IPlayer> startPlayers = new ArrayList<>(players);
        Collections.shuffle(startPlayers);
        int acutalNoofPlayers = Math.min(noofPlayers, startPlayers.size());
        for (int i = 0; i < acutalNoofPlayers; i++) {
            randomPlayers.add(startPlayers.get(i));
        }
        return randomPlayers;
    }

    public static int getNoofPlayersAdvancing(int noofPlayers, int maxPlayersPerGame) {
        int noofGames = getNoofGamesForPlayers(noofPlayers, maxPlayersPerGame);

        int[] playersPerGame = getPlayerDistribution(noofPlayers, noofGames);
        int playersOut = getNoofPlayersOut(maxPlayersPerGame);
        int sum = 0;
        for (int i = 0; i < playersPerGame.length; i++) {
            if (playersPerGame[i] + playersOut <= 0) {
                playersPerGame[i] = 1;
            } else {
                playersPerGame[i] += playersOut;
            }
            sum += playersPerGame[i];
        }
        return sum;
    }

    public static int[] getPlayerDistribution(int noofPlayers, int noofGames) {
        int[] playersPerGame = new int[noofGames];
        for (int i = 0; i < noofPlayers; i++) {
            playersPerGame[i%noofGames] += 1;
        }
        return playersPerGame;
    }

    public static int getNoofGamesForPlayers(int noofPlayers, int maxPlayersPerGame) {
        return (int) Math.ceil((double) noofPlayers / (double) maxPlayersPerGame);
    }

    public static int getNoofPlayersOut(int maxPlayersPerGame) {
        if (maxPlayersPerGame <= PLAYERS_SMALL)
            return -3;

        if (maxPlayersPerGame <= PLAYERS_MEDIUM)
            return -4;

        if (maxPlayersPerGame <= PLAYERS_LARGE)
            return -7;

        return -9;
    }

    public static int getMaxNoofPlayersPerGame(GameFeatures gameFeatures) {
        int gameSize = gameFeatures.getWidth() * gameFeatures.getHeight();

        /*
            5 players —> 25x25 (625)
            8 players —> 50x50 (2500)
            13 players —> 75x75 (5625)
            17 players —> 100x100 (10000)
         */
        if (gameSize <= 625) {
            return PLAYERS_SMALL;
        } else if (gameSize <= 2500) {
            return PLAYERS_MEDIUM;
        } else if (gameSize <= 5625) {
            return PLAYERS_LARGE;
        }
        return PLAYERS_XLARGE;
    }
}
