package se.cygni.snake.player;

import com.google.common.eventbus.EventBus;
import se.cygni.game.Player;
import se.cygni.game.WorldState;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.model.DeathReason;
import se.cygni.snake.apiconversion.GameMessageConverter;

public class RemotePlayer implements IPlayer {

    private Player player;
    private EventBus outgoingEventBus;
    private boolean alive = true;

    public RemotePlayer(Player player, EventBus outgoingEventBus) {
        this.player = player;
        this.outgoingEventBus = outgoingEventBus;
    }

    @Override
    public void onWorldUpdate(WorldState worldState, String gameId, long gameTick) {

        MapUpdateEvent mue = GameMessageConverter.onWorldUpdate(worldState, gameId, gameTick);
        mue.setReceivingPlayerId(player.getPlayerId());

        outgoingEventBus.post(mue);
    }

    @Override
    public void onPlayerDied(DeathReason reason, String playerId, int x, int y, String gameId, long gameTick) {

        SnakeDeadEvent sde = GameMessageConverter.onPlayerDied(reason, playerId, x, y, gameId, gameTick);
        sde.setReceivingPlayerId(player.getPlayerId());

        outgoingEventBus.post(sde);
    }

    @Override
    public void onGameEnded(String playerWinnerId, String gameId, long gameTick, WorldState worldState) {

        GameEndedEvent gee = GameMessageConverter.onGameEnded(playerWinnerId, gameId, gameTick, worldState);
        gee.setReceivingPlayerId(player.getPlayerId());

        outgoingEventBus.post(gee);
    }

    @Override
    public void onGameStart(String gameId, int noofPlayers, int width, int height) {

        GameStartingEvent gse = GameMessageConverter.onGameStart(gameId, noofPlayers, width, height);
        gse.setReceivingPlayerId(player.getPlayerId());

        outgoingEventBus.post(gse);
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
        return player.getName();
    }

    @Override
    public String getColor() {
        return player.getColor();
    }

    @Override
    public String getPlayerId() {
        return player.getPlayerId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RemotePlayer that = (RemotePlayer) o;

        return player != null ? player.equals(that.player) : that.player == null;

    }

    @Override
    public int hashCode() {
        return player != null ? player.hashCode() : 0;
    }
}
