package se.cygni.snake.player;

import se.cygni.snake.api.event.*;
import se.cygni.snake.api.model.PointReason;

public interface IPlayer extends Comparable<IPlayer> {

    void onWorldUpdate(MapUpdateEvent mapUpdateEvent);

    void onSnakeDead(SnakeDeadEvent snakeDeadEvent);

    void onGameEnded(GameEndedEvent gameEndedEvent);

    void onGameStart(GameStartingEvent gameStartingEvent);

    void onTournamentEnded(TournamentEndedEvent tournamentEndedEvent);

    void lostConnection(long gameTick);

    boolean isAlive();

    long getDiedAtTick();

    boolean isConnected();

    boolean isInTournament();

    void outOfTournament();

    void dead(long gameTick);

    void revive();

    String getName();

    String getPlayerId();

    void addPoints(PointReason reason, int points);

    void reset();

    int getTotalPoints();

    int getPointsBy(PointReason reason);
}
