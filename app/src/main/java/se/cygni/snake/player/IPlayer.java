package se.cygni.snake.player;

import se.cygni.snake.api.event.*;
import se.cygni.snake.api.model.PointReason;

public interface IPlayer {

    void onWorldUpdate(MapUpdateEvent mapUpdateEvent);

    void onSnakeDead(SnakeDeadEvent snakeDeadEvent);

    void onGameEnded(GameEndedEvent gameEndedEvent);

    void onGameStart(GameStartingEvent gameStartingEvent);

    void onTournamentEnded(TournamentEndedEvent tournamentEndedEvent);

    void lostConnection();

    boolean isAlive();

    boolean isConnected();

    boolean isInTournament();

    void outOfTournament();

    void dead();

    void revive();

    String getName();

    String getPlayerId();

    void addPoints(PointReason reason, int points);

    void resetPoints();

    int getTotalPoints();;
}
