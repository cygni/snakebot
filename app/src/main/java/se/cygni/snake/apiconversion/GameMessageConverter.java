package se.cygni.snake.apiconversion;

import se.cygni.game.WorldState;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.model.DeathReason;

public class GameMessageConverter {

    public static MapUpdateEvent onWorldUpdate(WorldState worldState, String gameId, long gameTick) {

        MapUpdateEvent mue = new MapUpdateEvent(
                gameTick,
                gameId,
                WorldStateConverter.convertWorldState(worldState, gameTick));

        return mue;
    }

    public static SnakeDeadEvent onPlayerDied(DeathReason reason, String playerId, int x, int y, String gameId, long gameTick) {

        SnakeDeadEvent sde = new SnakeDeadEvent(reason, playerId, x, y, gameId, gameTick);

        return sde;
    }

    public static GameEndedEvent onGameEnded(String playerWinnerId, String gameId, long gameTick, WorldState worldState) {

        GameEndedEvent gee = new GameEndedEvent(
                playerWinnerId, gameId, gameTick,
                WorldStateConverter.convertWorldState(worldState, gameTick)
        );

        return gee;
    }

    public static GameStartingEvent onGameStart(String gameId, int noofPlayers, int width, int height) {

        GameStartingEvent gse = new GameStartingEvent(gameId, noofPlayers, width, height);

        return gse;
    }
}
