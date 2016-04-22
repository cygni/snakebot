package se.cygni.snake.player;

import com.google.common.eventbus.EventBus;
import se.cygni.game.Player;
import se.cygni.snake.api.event.*;

public class RemotePlayer extends BasePlayer {

    private Player player;
    private EventBus outgoingEventBus;

    public RemotePlayer(Player player, EventBus outgoingEventBus) {
        super();
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

    @Override
    public void onTournamentEnded(TournamentEndedEvent tournamentEndedEvent) {
        TournamentEndedEvent tee = new TournamentEndedEvent(tournamentEndedEvent);
        tee.setReceivingPlayerId(player.getPlayerId());
        outgoingEventBus.post(tee);
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
