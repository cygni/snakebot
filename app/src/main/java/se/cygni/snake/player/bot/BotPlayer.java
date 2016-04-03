package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.model.PointReason;
import se.cygni.snake.player.IPlayer;

public abstract class BotPlayer implements IPlayer {

    private boolean alive = true;
    protected final String playerId;
    protected final EventBus incomingEventbus;
    private int accumulatedPoints = 0;

    public BotPlayer(String playerId, EventBus incomingEventbus) {
        this.playerId = playerId;
        this.incomingEventbus = incomingEventbus;
    }

    @Override
    public void onWorldUpdate(MapUpdateEvent mapUpdateEvent) {

    }

    @Override
    public void onSnakeDead(SnakeDeadEvent snakeDeadEvent) {

    }

    @Override
    public void onGameEnded(GameEndedEvent gameEndedEvent) {

    }

    @Override
    public void onGameStart(GameStartingEvent gameStartingEvent) {

    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void dead() {
        alive = false;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }

    @Override
    public void addPoints(PointReason reason, int points) {
        accumulatedPoints += points;
    }

    @Override
    public int getTotalPoints() {
        return accumulatedPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BotPlayer botPlayer = (BotPlayer) o;

        return playerId != null ? playerId.equals(botPlayer.playerId) : botPlayer.playerId == null;

    }

    @Override
    public int hashCode() {
        return playerId != null ? playerId.hashCode() : 0;
    }
}
