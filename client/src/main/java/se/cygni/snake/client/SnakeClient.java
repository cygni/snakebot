package se.cygni.snake.client;

import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.response.PlayerRegistered;

public interface SnakeClient {

    public void onMapUpdate(MapUpdateEvent mapUpdateEvent);

    public void onSnakeDead(SnakeDeadEvent snakeDeadEvent);

    public void onGameEnded(GameEndedEvent gameEndedEvent);

    public void onGameStarting(GameStartingEvent gameStartingEvent);

    public void onPlayerRegistered(PlayerRegistered playerRegistered);

    public void onInvalidPlayerName(InvalidPlayerName invalidPlayerName);

    public String getServerHost();

    public int getServerPort();

    public void onConnected();

    public void onSessionClosed();

    public String getName();

    public String getColor();

    public GameMode getGameMode();

}
