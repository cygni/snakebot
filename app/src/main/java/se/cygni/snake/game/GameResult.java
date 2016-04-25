package se.cygni.snake.game;

import se.cygni.snake.player.IPlayer;

import java.util.*;

public class GameResult {

    private List<PlayerScore> result = new ArrayList<>();

    private int highestScore = Integer.MIN_VALUE;
    private IPlayer winner;

    public void addResult(int score, IPlayer player) {
        result.add(new PlayerScore(score, player));
        Collections.sort(result, (o1, o2) -> o1.getPlayer().compareTo(o2.getPlayer()));

        if (score > highestScore) {
            highestScore = score;
            winner = player;
        }
    }

    public List<IPlayer> getSortedResult() {
        List<IPlayer> sortedList = new ArrayList<>();

        Iterator<PlayerScore> iter = result.iterator();
        while (iter.hasNext()) {
            sortedList.add(iter.next().getPlayer());
        }
        return sortedList;
    }

    public IPlayer getWinner() {
        return winner;
    }

    private class PlayerScore {
        private final int score;
        private final IPlayer player;

        public PlayerScore(int score, IPlayer player) {
            this.score = score;
            this.player = player;
        }

        public int getScore() {
            return score;
        }

        public IPlayer getPlayer() {
            return player;
        }
    }
}
