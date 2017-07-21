package se.cygni.snake.player.bot.brain;

import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daniel Eineving on 2017-04-03.
 */
public class BrainTools {
    public static List<SnakeDirection> filledMoves(MapUtil mapUtil) {
        List<SnakeDirection> filled = new LinkedList<>();
        filled.add(SnakeDirection.DOWN);
        filled.add(SnakeDirection.UP);
        filled.add(SnakeDirection.RIGHT);
        filled.add(SnakeDirection.LEFT);
        return filled;
    }

    public static List<MapCoordinate> getTheFourNeighbours(MapCoordinate root) {
        List<MapCoordinate> neighbours = new ArrayList<>();
        neighbours.add(root.translateBy(-1, 0));
        neighbours.add(root.translateBy(1, 0));
        neighbours.add(root.translateBy(0, 1));
        neighbours.add(root.translateBy(0, -1));
        return neighbours;
    }

    public static List<MapCoordinate> getPossibleEnemySnakePossitions(MapUtil mapUtil, List<String> liveSnakes) {
        List<MapCoordinate> enemyPossibleNext = new LinkedList<>();
        MapCoordinate[] heads = getEnemyHeads(mapUtil, liveSnakes);


        for (MapCoordinate enemyHead : heads) {
            enemyPossibleNext.add(enemyHead.translateBy(-1, 0));
            enemyPossibleNext.add(enemyHead.translateBy(1, 0));
            enemyPossibleNext.add(enemyHead.translateBy(0, 1));
            enemyPossibleNext.add(enemyHead.translateBy(0, -1));
        }
        return enemyPossibleNext;
    }

    public static MapCoordinate[] getEnemyHeads(MapUtil mapUtil, List<String> liveSnakes) {
        MapCoordinate[] heads = new MapCoordinate[liveSnakes.size() - 1];
        int i = 0;
        for (String snake : liveSnakes) {
            MapCoordinate temp = mapUtil.getSnakeSpread(snake)[0];
            if (!temp.equals(mapUtil.getMyPosition())) {
                heads[i] = temp;
                i++;
            }
        }
        return heads;
    }
}
