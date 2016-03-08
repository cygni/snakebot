package se.cygni.snake.player;

import se.cygni.game.WorldState;
import se.cygni.snake.api.model.DeathReason;

public interface IPlayer {

    public void onWorldUpdate(WorldState worldState, String gameId, long gameTick);

    public void onPlayerDied(DeathReason reason, String playerId, int x, int y, String gameId, long gameTick);

    public void onGameEnded(String playerWinnerId, String gameId, long gameTick, WorldState worldState);

    public void onGameStart(String gameId, int noofPlayers, int width, int height);

    public boolean isAlive();

    public void dead();

    public String getName();

    public String getColor();

    public String getPlayerId();
}
