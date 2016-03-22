package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;
import se.cygni.game.WorldState;
import se.cygni.snake.api.model.DeathReason;
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
    public void onWorldUpdate(WorldState worldState, String gameId, long gameTick) {

    }

    @Override
    public void onPlayerDied(DeathReason reason, String playerId, int x, int y, String gameId, long gameTick) {

    }

    @Override
    public void onGameEnded(String playerWinnerId, String gameId, long gameTick, WorldState worldState) {

    }

    @Override
    public void onGameStart(String gameId, int noofPlayers, int width, int height) {

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
    public String getColor() {
        return null;
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
