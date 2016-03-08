package se.cygni.snake.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.model.DeathReason;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class SnakeDeadEvent extends GameMessage {

    private final DeathReason deathReason;
    private final String playerId;
    private final int x, y;
    private final String gameId;
    private final long gameTick;

    @JsonCreator
    public SnakeDeadEvent(
            @JsonProperty("deathReason") DeathReason deathReason,
            @JsonProperty("playerId") String playerId,
            @JsonProperty("x") int x,
            @JsonProperty("y") int y,
            @JsonProperty("gameId") String gameId,
            @JsonProperty("gameTick") long gameTick) {

        this.deathReason = deathReason;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.gameId = gameId;
        this.gameTick = gameTick;
    }

    /**
     *
     * @return the reason for this death
     */
    public DeathReason getDeathReason() {
        return deathReason;
    }

    /**
     *
     * @return id of the snake that died
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     *
     * @return x coordinate for where the snake head collided
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return y coordinate for where the snake head collided
     */
    public int getY() {
        return y;
    }

    /**
     *
     * @return gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     *
     * @return gameTick
     */
    public long getGameTick() {
        return gameTick;
    }

    @Override
    public String toString() {
        return "SnakeDeadEvent{" +
                "deathReason=" + deathReason +
                ", playerId='" + playerId + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", gameId='" + gameId + '\'' +
                ", gameTick=" + gameTick +
                '}';
    }
}
