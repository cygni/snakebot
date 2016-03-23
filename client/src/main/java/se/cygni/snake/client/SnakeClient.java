package se.cygni.snake.client;

import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.response.PlayerRegistered;

public interface SnakeClient {

    void onMapUpdate(MapUpdateEvent mapUpdateEvent);

    void onSnakeDead(SnakeDeadEvent snakeDeadEvent);

    void onGameEnded(GameEndedEvent gameEndedEvent);

     void onGameStarting(GameStartingEvent gameStartingEvent);

     void onPlayerRegistered(PlayerRegistered playerRegistered);

     void onInvalidPlayerName(InvalidPlayerName invalidPlayerName);

     String getServerHost();

     int getServerPort();

     void onConnected();

     void onSessionClosed();

     String getName();

     String getColor();

     GameMode getGameMode();

}
