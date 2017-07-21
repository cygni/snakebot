package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.client.MapUtil;
import se.cygni.snake.player.bot.brain.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BrainySnakePlayer extends BotPlayer {

    private MapUtil mapUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(BrainySnakePlayer.class);
    private List<Sense> senses = new LinkedList<>();

    @Override
    public void onWorldUpdate(MapUpdateEvent mapUpdateEvent) {
        CompletableFuture cf = CompletableFuture.runAsync(() -> {
            //postNextMove(mapUpdateEvent.getGameId(), mapUpdateEvent.getMap(), mapUpdateEvent.getGameTick());
            mapUtil = new MapUtil(mapUpdateEvent.getMap(), getPlayerId());

            SnakeInfo[] snakeInfo = mapUpdateEvent.getMap().getSnakeInfos();

            List<String> liveSnakeIDs = new ArrayList<>();

            for (SnakeInfo snake : snakeInfo) {
                if (snake.isAlive()) {
                    liveSnakeIDs.add(snake.getId());
                }
            }
            List<java.util.Map<SnakeDirection, Double>> sensePrios = new LinkedList<>();

            for (Sense sense : senses) {
                sensePrios.add(sense.getMovesRanked(mapUtil, liveSnakeIDs));
            }


            Double up = 1.0, down = 1.0, left = 1.0, right = 1.0;
            for (java.util.Map<SnakeDirection, Double> instance : sensePrios) {
                up *= instance.get(SnakeDirection.UP);
                down *= instance.get(SnakeDirection.DOWN);
                left *= instance.get(SnakeDirection.LEFT);
                right *= instance.get(SnakeDirection.RIGHT);
            }

            SnakeDirection moveToMake = SnakeDirection.UP;
            if (up == 0 && down == 0 && right == 0 && left == 0) {
                moveToMake = SnakeDirection.UP;
            } else if (up >= down && up >= right && up >= left) {
                moveToMake = SnakeDirection.UP;
            } else if (down >= up && down >= right && down >= left) {
                moveToMake = SnakeDirection.DOWN;
            } else if (right >= up && right >= down && right >= left) {
                moveToMake = SnakeDirection.RIGHT;
            } else if (left >= up && left >= right && left >= down) {
                moveToMake = SnakeDirection.LEFT;
            }

            RegisterMove registerMove = new RegisterMove(mapUpdateEvent.getGameId(), mapUpdateEvent.getGameTick(), moveToMake);
            registerMove.setReceivingPlayerId(playerId);
            incomingEventbus.post(registerMove);
        });
    }


    public BrainySnakePlayer(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
        senses.add(new Obvious());
        senses.add(new Caution(0.1));
        senses.add(new Planning(0.25));
        senses.add(new CageFright(1.0, 0.95));
        senses.add(new Fear(3, 0.1));
    }
}
