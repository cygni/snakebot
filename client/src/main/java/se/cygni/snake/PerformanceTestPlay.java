package se.cygni.snake;

import java.util.ArrayList;
import java.util.List;

public class PerformanceTestPlay {

    public static void main(String[] args) {


        Runnable task = () -> {

            List<ExampleSnakePlayer> players = new ArrayList<>();

            int noofPlayers = 14;
            for (int i = 0; i < noofPlayers; i++) {
                ExampleSnakePlayer player = new ExampleSnakePlayer();
                player.connect();
                players.add(player);
            }

            // Keep this process alive as long as the
            // Snakes are playing.
            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!allPlayersDone(players));
        };


        Thread thread = new Thread(task);
        thread.start();
    }

    private static boolean allPlayersDone(List<ExampleSnakePlayer> players) {
        for (ExampleSnakePlayer player : players) {
            if (player.isPlaying()) {
                return false;
            }
        }
        return true;
    }
}
