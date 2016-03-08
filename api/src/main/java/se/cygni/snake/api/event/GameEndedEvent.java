package se.cygni.snake.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class GameEndedEvent extends GameMessage {

    private final String playerWinnerId;
    private final String gameId;
    private final long gameTick;
    private final Map map;


    @JsonCreator
    public GameEndedEvent(
            @JsonProperty("playerWinnerId") String playerWinnerId,
            @JsonProperty("gameId") String gameId,
            @JsonProperty("gameTick") long gameTick,
            @JsonProperty("map") Map map) {

        this.playerWinnerId = playerWinnerId;
        this.gameId = gameId;
        this.gameTick = gameTick;
        this.map = map;
    }

    public String getPlayerWinnerId() {
        return playerWinnerId;
    }

    public String getGameId() {
        return gameId;
    }

    public long getGameTick() {
        return gameTick;
    }

    public Map getMap() {
        return map;
    }

    @Override
    public String toString() {
        return "GameEndedEvent{" +
                "playerWinnerId='" + playerWinnerId + '\'' +
                ", gameId='" + gameId + '\'' +
                ", gameTick=" + gameTick +
                ", map=\n" + map +
                '}';
    }
}
