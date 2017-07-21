package se.cygni.snake.player.bot.brain;

import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by danie on 2017-04-04.
 */
public class Centre extends Sense {

    private Double prefered;
    private Double second;
    private Double third;
    private Double forth;

    public Centre(Double prefered, Double second, Double third, Double forth) {
        this.prefered = prefered;
        this.second = second;
        this.third = third;
        this.forth = forth;
    }


    @Override
    public Map<SnakeDirection, Double> getMovesRanked(MapUtil mapUtil, List<String> liveSnakes) {
        return null;
    }
}
