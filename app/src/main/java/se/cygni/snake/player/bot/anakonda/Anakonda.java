package se.cygni.snake.player.bot.anakonda;

import static se.cygni.snake.player.bot.anakonda.maxmin.MaxMin.bestDirection2;

import java.util.concurrent.CompletableFuture;

import com.google.common.eventbus.EventBus;

import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.player.bot.BotPlayer;

public class Anakonda extends BotPlayer {

    private String name;
    public Anakonda(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
        this.name = "\uD83D\uDC0D Anakonda" + Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    private int snakesAlive(MapUpdateEvent mapUpdateEvent) {
        int numAlive = 0;
        for (SnakeInfo si : mapUpdateEvent.getMap().getSnakeInfos()) {
            if (si.isAlive()) {
                numAlive++;
            }
        }

        return numAlive;
    }

    @Override
    public void onWorldUpdate(MapUpdateEvent mapUpdateEvent) {
        CompletableFuture cf = CompletableFuture.runAsync(() -> {        
            int numAlive = snakesAlive(mapUpdateEvent);
            int tick = (int) mapUpdateEvent.getGameTick();
            
            if (tick == 0) {
                RegisterMove registerMove = new RegisterMove(mapUpdateEvent.getGameId(), mapUpdateEvent.getGameTick(),
                        SnakeDirection.DOWN);
                registerMove.setReceivingPlayerId(playerId);
                incomingEventbus.post(registerMove);
                return;
            }
            State s = new State(mapUpdateEvent.getMap(), this.name);

            int steps = 2;
            int utility_look = 10;
            switch (numAlive) {
            case 2:
                steps = 4;
                utility_look = 800;
                break;
            case 3:
                steps = 4;
                utility_look = 150;
                break;
            case 4:
                steps = 2;
                utility_look = 400;
                break;
            case 5:
                steps = 2;
                utility_look = 20;
            }

            

            String dir = bestDirection2(s, steps, utility_look);

            SnakeDirection chosenDirection;
            switch (dir) {
            case "RIGHT":
                chosenDirection = SnakeDirection.RIGHT;
                break;
            case "LEFT":
                chosenDirection = SnakeDirection.LEFT;
                break;
            case "UP":
                chosenDirection = SnakeDirection.UP;
                break;
            case "DOWN":
                chosenDirection = SnakeDirection.DOWN;
                break;
            default:
                chosenDirection = SnakeDirection.DOWN;
            }

            RegisterMove registerMove = new RegisterMove(mapUpdateEvent.getGameId(), mapUpdateEvent.getGameTick(),
                    chosenDirection);
            registerMove.setReceivingPlayerId(playerId);
            incomingEventbus.post(registerMove);
        });
    }

    @Override
    public String getName() {
        return this.name;
    }
}
