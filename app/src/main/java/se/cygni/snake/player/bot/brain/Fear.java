package se.cygni.snake.player.bot.brain;

import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel Eineving on 2017-04-05.
 */
public class Fear extends Sense {

    private int manhattanDistanceCap;
    private Double reductionStepPerClose;

    public Fear(int manhattanDistanceCap, Double reductionStepPerClose) {
        this.manhattanDistanceCap = manhattanDistanceCap;
        this.reductionStepPerClose = reductionStepPerClose;
    }

    @Override
    public Map<SnakeDirection, Double> getMovesRanked(MapUtil mapUtil, List<String> liveSnakes) {
        MapCoordinate[] enemyHeads = BrainTools.getEnemyHeads(mapUtil, liveSnakes);
        MapCoordinate myHead = mapUtil.getMyPosition();
        Map<SnakeDirection, Double> prios = getPrioTemplate();


        for (MapCoordinate enemyHead : enemyHeads) {
            if (enemyHead.getManhattanDistanceTo(myHead) <= manhattanDistanceCap) {
                if (myHead.x < enemyHead.x) {
                    prios.put(SnakeDirection.RIGHT, prios.get(SnakeDirection.RIGHT) - reductionStepPerClose);
                } else {
                    prios.put(SnakeDirection.LEFT, prios.get(SnakeDirection.LEFT) - reductionStepPerClose);
                }

                if (myHead.y < enemyHead.y) {
                    prios.put(SnakeDirection.DOWN, prios.get(SnakeDirection.DOWN) - reductionStepPerClose);
                } else {
                    prios.put(SnakeDirection.UP, prios.get(SnakeDirection.UP) - reductionStepPerClose);
                }
            }
        }
        return prios;
    }
}
