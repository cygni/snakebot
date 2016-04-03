package se.cygni.snake.player;

import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.model.PointReason;

public interface IPlayer {

    /*
    void onWorldUpdate(WorldState worldState, String gameId, long gameTick, Set<IPlayer> players);

    void onPlayerDied(DeathReason reason, String playerId, int x, int y, String gameId, long gameTick);

    void onGameEnded(String playerWinnerId, String gameId, long gameTick, WorldState worldState, Set<IPlayer> players);

    void onGameStart(String gameId, int noofPlayers, int width, int height);
*/
    void onWorldUpdate(MapUpdateEvent mapUpdateEvent);

    void onSnakeDead(SnakeDeadEvent snakeDeadEvent);

    void onGameEnded(GameEndedEvent gameEndedEvent);

    void onGameStart(GameStartingEvent gameStartingEvent);

    boolean isAlive();

    void dead();

    String getName();

    String getPlayerId();

    void addPoints(PointReason reason, int points);

    int getTotalPoints();
}
