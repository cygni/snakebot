package se.cygni.snake.event;

import se.cygni.game.WorldState;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.model.DeathReason;
import se.cygni.snake.apiconversion.GameMessageConverter;
import se.cygni.snake.player.IPlayer;

import java.util.Set;

public class InternalGameEvent {
    private final long tstamp;
    private GameMessage gameMessage;

    public InternalGameEvent(long tstamp) {
        this.tstamp = tstamp;
    }

    public InternalGameEvent(long tstamp, GameMessage gameMessage) {
        this.tstamp = tstamp;
        this.gameMessage = gameMessage;
    }

    public long getTstamp() {
        return tstamp;
    }

    public GameMessage getGameMessage() {
        return gameMessage;
    }

    public void onWorldUpdate(WorldState worldState, String gameId, long gameTick, Set<IPlayer> players) {
        this.gameMessage = GameMessageConverter.onWorldUpdate(worldState, gameId, gameTick, players);
    }

    public void onPlayerDied(DeathReason reason, String playerId, int x, int y, String gameId, long gameTick) {
        this.gameMessage = GameMessageConverter.onPlayerDied(reason, playerId, x, y, gameId, gameTick);
    }

    public void onGameEnded(String playerWinnerId, String gameId, long gameTick, WorldState worldState, Set<IPlayer> players) {
        this.gameMessage = GameMessageConverter.onGameEnded(playerWinnerId, gameId, gameTick, worldState, players);
    }

    public void onGameStart(String gameId, int noofPlayers, int width, int height) {
        this.gameMessage = GameMessageConverter.onGameStart(gameId, noofPlayers, width, height);
    }
}
