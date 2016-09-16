package se.cygni.snake.apiconversion;

import se.cygni.game.WorldState;
import se.cygni.snake.api.event.*;
import se.cygni.snake.api.model.DeathReason;
import se.cygni.snake.game.GameFeatures;
import se.cygni.snake.player.IPlayer;

import java.util.Set;

public class GameMessageConverter {

    public static MapUpdateEvent onWorldUpdate(WorldState worldState, String gameId, long gameTick, Set<IPlayer> players) {
        return new MapUpdateEvent(
                gameTick,
                gameId,
                WorldStateConverter.convertWorldState(worldState, gameTick, players));
    }

    public static SnakeDeadEvent onPlayerDied(DeathReason reason, String playerId, int x, int y, String gameId, long gameTick) {
        return new SnakeDeadEvent(reason, playerId, x, y, gameId, gameTick);
    }

    public static GameEndedEvent onGameEnded(String playerWinnerId, String gameId, long gameTick, WorldState worldState, Set<IPlayer> players) {
        return new GameEndedEvent(
                playerWinnerId, gameId, gameTick,
                WorldStateConverter.convertWorldState(worldState, gameTick, players));
    }

    public static GameStartingEvent onGameStart(String gameId, int noofPlayers, int width, int height, GameFeatures gameFeatures) {
        return new GameStartingEvent(gameId, noofPlayers, width, height, GameSettingsConverter.toGameSettings(gameFeatures));
    }

    public static GameAbortedEvent onGameAborted(String gameId) {
        return new GameAbortedEvent(gameId);
    }

    public static GameChangedEvent onGameChanged(String gameId) {
        return new GameChangedEvent(gameId);
    }
}
