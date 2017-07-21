package se.cygni.snake.player.bot.brain;

import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;

import java.util.*;

/**
 * Created by Daniel Eineving on 2017-04-03.
 */
public class Planning extends Sense {
    private Set<MapCoordinate> tempBounding = new HashSet<>();


    public Planning(Double increment) {
        this.increment = increment;
    }

    private Double increment;

    @Override
    public Map<SnakeDirection, Double> getMovesRanked(MapUtil mapUtil, List<String> liveSnakes) {
        //bounding box sizes
        int up = 0, down = 0, left = 0, right = 0;

        if (mapUtil.canIMoveInDirection(SnakeDirection.UP)) {
            up = maxIf(mapUtil, mapUtil.getMyPosition().translateBy(0, -1));
        }
        if (mapUtil.canIMoveInDirection(SnakeDirection.DOWN)) {
            down = maxIf(mapUtil, mapUtil.getMyPosition().translateBy(0, 1));
        }
        if (mapUtil.canIMoveInDirection(SnakeDirection.RIGHT)) {
            right = maxIf(mapUtil, mapUtil.getMyPosition().translateBy(1, 0));
        }
        if (mapUtil.canIMoveInDirection(SnakeDirection.LEFT)) {
            left = maxIf(mapUtil, mapUtil.getMyPosition().translateBy(-1, 0));
        }

        return getSnakeDirections(up, down, left, right);
    }

    private int maxIf(MapUtil mapUtil, MapCoordinate proposedMove) {

        int max = 1;
        int temp = 0;
        for (MapCoordinate next : BrainTools.getTheFourNeighbours(proposedMove)) {
            tempBounding = new HashSet<>();
            tempBounding.add(proposedMove);
            temp = wrapped(mapUtil, next);
            if (max < temp) {
                max = temp;
            }
        }
        return max;
    }

    private Map<SnakeDirection, Double> getSnakeDirections(int up, int down, int left, int right) {
        Map<SnakeDirection, Double> prios = new HashMap<>();
        TreeMap<Integer, List<SnakeDirection>> tree = new TreeMap<>();


        //UP
        List<SnakeDirection> first = new LinkedList<>();
        first.add(SnakeDirection.UP);
        tree.put(up, first);

        //DOWN
        List<SnakeDirection> temp = tree.get(down);
        if (temp != null) {
            temp.add(SnakeDirection.DOWN);
        } else {
            List<SnakeDirection> tempList = new LinkedList<>();
            tempList.add(SnakeDirection.DOWN);
            tree.put(down, tempList);
        }

        //LEFT
        temp = tree.get(left);
        if (temp != null) {
            temp.add(SnakeDirection.LEFT);
        } else {
            List<SnakeDirection> tempList = new LinkedList<>();
            tempList.add(SnakeDirection.LEFT);
            tree.put(left, tempList);
        }


        //RIGHT
        temp = tree.get(right);
        if (temp != null) {
            temp.add(SnakeDirection.RIGHT);
        } else {
            List<SnakeDirection> tempList = new LinkedList<>();
            tempList.add(SnakeDirection.RIGHT);
            tree.put(right, tempList);
        }

        Double currentPrio = 0.0;
        for (Map.Entry<Integer, List<SnakeDirection>> entry : tree.entrySet()) {
            for (SnakeDirection direction : entry.getValue()) {
                prios.put(direction, currentPrio);
            }
            currentPrio += increment;
        }
        return prios;
    }

    private int wrapped(MapUtil mapUtil, MapCoordinate start) {
        if (tempBounding.add(start) && mapUtil.isTileAvailableForMovementTo(start)) {
            recursive(mapUtil, start.translateBy(0, -1));
            recursive(mapUtil, start.translateBy(0, 1));
            recursive(mapUtil, start.translateBy(1, 0));
            recursive(mapUtil, start.translateBy(-1, 0));
        }
        return tempBounding.size();

    }

    private void recursive(MapUtil mapUtil, MapCoordinate coordinate) {
        if (mapUtil.isTileAvailableForMovementTo(coordinate)) {
            if (tempBounding.add(coordinate)) {
                recursive(mapUtil, coordinate.translateBy(0, -1));
                recursive(mapUtil, coordinate.translateBy(0, 1));
                recursive(mapUtil, coordinate.translateBy(1, 0));
                recursive(mapUtil, coordinate.translateBy(-1, 0));
            }
        }
    }
}
