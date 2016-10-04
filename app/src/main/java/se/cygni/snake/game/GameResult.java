package se.cygni.snake.game;

import se.cygni.snake.player.IPlayer;
import se.cygni.snake.player.IPlayerComparator;

import java.util.*;

public class GameResult {

    SortedSet<IPlayer> result = new TreeSet<>(new IPlayerComparator());

    public void addResult(IPlayer player) {
        result.add(player);
    }

    public List<IPlayer> getSortedResult() {
        List<IPlayer> sortedList = new ArrayList<>();

        Iterator<IPlayer> iter = result.iterator();
        while (iter.hasNext()) {
            sortedList.add(iter.next());
        }
        return sortedList;
    }

    public IPlayer getWinner() {
        return result.first();
    }

}
