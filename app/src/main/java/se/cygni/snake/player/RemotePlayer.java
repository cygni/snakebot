package se.cygni.snake.player;

import com.google.common.eventbus.EventBus;
import se.cygni.game.Player;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.model.PointReason;

public class RemotePlayer implements IPlayer {

    private Player player;
    private EventBus outgoingEventBus;
    private boolean alive = true;
    private int accumulatedPoints = 0;

    public RemotePlayer(Player player, EventBus outgoingEventBus) {
        this.player = player;
        this.outgoingEventBus = outgoingEventBus;
    }

    @Override
    public void onWorldUpdate(MapUpdateEvent mue) {
        MapUpdateEvent mapUpdateEvent = new MapUpdateEvent(mue);
        mapUpdateEvent.setReceivingPlayerId(player.getPlayerId());
        outgoingEventBus.post(mapUpdateEvent);
    }

    @Override
    public void onSnakeDead(SnakeDeadEvent sde) {
        SnakeDeadEvent snakeDeadEvent = new SnakeDeadEvent(sde);
        snakeDeadEvent.setReceivingPlayerId(player.getPlayerId());
        outgoingEventBus.post(snakeDeadEvent);
    }

    @Override
    public void onGameEnded(GameEndedEvent gee) {
        GameEndedEvent gameEndedEvent = new GameEndedEvent(gee);
        gameEndedEvent.setReceivingPlayerId(player.getPlayerId());
        outgoingEventBus.post(gameEndedEvent);
    }

    @Override
    public void onGameStart(GameStartingEvent gse) {
        GameStartingEvent gameStartingEvent = new GameStartingEvent(gse);
        gameStartingEvent.setReceivingPlayerId(player.getPlayerId());
        outgoingEventBus.post(gameStartingEvent);
    }

    /*
    @Override
    public void onWorldUpdate(WorldState worldState, String gameId, long gameTick, Set<IPlayer> players) {

        MapUpdateEvent mue = GameMessageConverter.onWorldUpdate(worldState, gameId, gameTick, players);
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
    public void onGameEnded(String playerWinnerId, String gameId, long gameTick, WorldState worldState, Set<IPlayer> players) {

        GameEndedEvent gee = GameMessageConverter.onGameEnded(playerWinnerId, gameId, gameTick, worldState, players);
        gee.setReceivingPlayerId(player.getPlayerId());

        outgoingEventBus.post(gee);
    }

    @Override
    public void onGameStart(String gameId, int noofPlayers, int width, int height) {

        GameStartingEvent gse = GameMessageConverter.onGameStart(gameId, noofPlayers, width, height);
        gse.setReceivingPlayerId(player.getPlayerId());

        outgoingEventBus.post(gse);
    }
*/
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
    public String getPlayerId() {
        return player.getPlayerId();
    }

    @Override
    public void addPoints(PointReason reason, int points) {
        accumulatedPoints += points;
    }

    @Override
    public int getTotalPoints() {
        return accumulatedPoints;
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
