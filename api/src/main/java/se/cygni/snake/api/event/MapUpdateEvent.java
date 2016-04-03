package se.cygni.snake.api.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.api.type.GameMessageType;

@GameMessageType
public class MapUpdateEvent extends GameMessage {

    private final long gameTick;
    private final String gameId;
    private final Map map;

    @JsonCreator
    public MapUpdateEvent(
            @JsonProperty("gameTick") long gameTick,
            @JsonProperty("gameId") String gameId,
            @JsonProperty("map") Map map) {

        this.gameTick = gameTick;
        this.gameId = gameId;
        this.map = map;
    }

    public MapUpdateEvent(MapUpdateEvent mue) {
        this.gameTick = mue.getGameTick();
        this.gameId = mue.getGameId();
        this.map = mue.getMap();
    }

    public long getGameTick() {
        return gameTick;
    }

    public String getGameId() {
        return gameId;
    }

    public Map getMap() {
        return map;
    }

    @Override
    public String toString() {
        return "MapUpdateEvent{" +
                "gameTick=" + gameTick +
                ", gameId='" + gameId + '\'' +
                ", map=\n" + map +
                '}';
    }
}
