package se.cygni.snake.player.bot.brain;

import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;

import java.util.List;
import java.util.Map;

/**
 * Checks for no head crash.
 */
public class Caution extends Sense {
    Double reductionFactor;

    public Caution(Double reductionFactor) {
        this.reductionFactor = reductionFactor;
    }

    @Override
    public Map<SnakeDirection, Double> getMovesRanked(MapUtil mapUtil, List<String> liveSnakes) {

        Map<SnakeDirection, Double> weighted = getPrioTemplate();


        List<MapCoordinate> enemyPossibleNext = BrainTools.getPossibleEnemySnakePossitions(mapUtil, liveSnakes);

        if (enemyPossibleNext.contains(mapUtil.getMyPosition().translateBy(-1, 0))) {
            weighted.put(SnakeDirection.LEFT, reductionFactor);
        }
        if (enemyPossibleNext.contains(mapUtil.getMyPosition().translateBy(1, 0))) {
            weighted.put(SnakeDirection.RIGHT, reductionFactor);
        }
        if (enemyPossibleNext.contains(mapUtil.getMyPosition().translateBy(0, 1))) {
            weighted.put(SnakeDirection.DOWN, reductionFactor);
        }
        if (enemyPossibleNext.contains(mapUtil.getMyPosition().translateBy(0, -1))) {
            weighted.put(SnakeDirection.UP, reductionFactor);
        }
        return weighted;
    }




}
