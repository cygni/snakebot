package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.api.model.PointReason;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.model.TileContent;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;
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

    /**
     * Adds scores for 1 to n tiles in the indicated direction, starting from where we are now.
     *
     * @param mapUtil current MapUtil class
     * @param potentialDirection which direction are we looking?
     * @param myPosition where am I now?
     * @param howFar how far can I see?
     */
    protected void addDirectionScore(final Map gameMap, final MapUtil mapUtil, final PotentialDirection potentialDirection, final MapCoordinate myPosition, final int howFar) {
        for (int i = 1; i <= howFar; i++) {
            MapCoordinate coordinate = possibleNewPosition(myPosition, potentialDirection.getDirection(), i);
            addTileToPotentialDirection(gameMap, mapUtil, potentialDirection, i, coordinate);

            if (i == 1) {
                if (potentialDirection.getDirection() == SnakeDirection.LEFT || potentialDirection.getDirection() == SnakeDirection.RIGHT) {
                    coordinate = coordinate.translateBy(1, 0);
                    addTileToPotentialDirection(gameMap, mapUtil, potentialDirection, i, coordinate);
                    coordinate = coordinate.translateBy(-1, 0);
                    addTileToPotentialDirection(gameMap, mapUtil, potentialDirection, i, coordinate);
                } else {
                    coordinate = coordinate.translateBy(0, 1);
                    addTileToPotentialDirection(gameMap, mapUtil, potentialDirection, i, coordinate);
                    coordinate = coordinate.translateBy(0, -1);
                    addTileToPotentialDirection(gameMap, mapUtil, potentialDirection, i, coordinate);
                }
            }
        }
    }

    private void addTileToPotentialDirection(Map gameMap, MapUtil mapUtil, PotentialDirection potentialDirection, int i, MapCoordinate coordinate) {
        if (mapUtil.isCoordinateOutOfBounds(coordinate)) {
            potentialDirection.applyOutOfBoundsScore();
        } else {
            TileContent tileContent = mapUtil.getTileAt(coordinate);
            potentialDirection.addTile(gameMap, tileContent, i, getPlayerId());
        }
    }

    /**
     * @param currentPosition snake's current coordinates
     * @param direction       direction the snake wants to investigate
     * @param howFar          how many tiles from the snake's current position
     * @return potential new coordinate
     */
    protected MapCoordinate possibleNewPosition(final MapCoordinate currentPosition, final SnakeDirection direction, final int howFar) {
        MapCoordinate newCoordinate = currentPosition.translateBy(0, 0);
        switch (direction) {
            case DOWN:
                newCoordinate = newCoordinate.translateBy(0, howFar);
                break;
            case UP:
                newCoordinate = newCoordinate.translateBy(0, -howFar);
                break;
            case LEFT:
                newCoordinate = newCoordinate.translateBy(-howFar, 0);
                break;
            case RIGHT:
                newCoordinate = newCoordinate.translateBy(howFar, 0);
        }
        return newCoordinate;
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
