package se.cygni.snake.player;

import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.event.*;
import se.cygni.snake.api.model.PointReason;

public interface IPlayer extends Comparable<IPlayer> {

    default void onGameMessage(GameMessage gameMessage) {
        if (gameMessage instanceof  MapUpdateEvent) {
            onWorldUpdate((MapUpdateEvent)gameMessage);
        } else if (gameMessage instanceof  SnakeDeadEvent) {
            onSnakeDead((SnakeDeadEvent)gameMessage);
        } else if (gameMessage instanceof  GameEndedEvent) {
            onGameEnded((GameEndedEvent)gameMessage);
        } else if (gameMessage instanceof  GameStartingEvent) {
            onGameStart((GameStartingEvent)gameMessage);
        } else if (gameMessage instanceof  TournamentEndedEvent) {
            onTournamentEnded((TournamentEndedEvent)gameMessage);
        }
    }

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
