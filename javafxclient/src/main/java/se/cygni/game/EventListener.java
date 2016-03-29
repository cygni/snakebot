package se.cygni.game;

import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.websocket.event.api.ActiveGamesList;

public interface EventListener {

    public void onMessage(String message);

    public void onActiveGamesList(ActiveGamesList activeGamesList);

    public void onMapUpdate(MapUpdateEvent mapUpdateEvent);

    public void onSnakeDead(SnakeDeadEvent snakeDeadEvent);

    public void onGameEnded(GameEndedEvent gameEndedEvent);

    public void onGameStarting(GameStartingEvent gameStartingEvent);

    public void onPlayerRegistered(PlayerRegistered playerRegistered);

    public void onInvalidPlayerName(InvalidPlayerName invalidPlayerName);

}
