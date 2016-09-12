package se.cygni.snake.client;

import se.cygni.snake.api.event.*;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.response.PlayerRegistered;

public interface SnakeClient {

    void onMapUpdate(MapUpdateEvent mapUpdateEvent);

    void onSnakeDead(SnakeDeadEvent snakeDeadEvent);

    void onGameEnded(GameEndedEvent gameEndedEvent);

    void onGameStarting(GameStartingEvent gameStartingEvent);

    void onTournamentEnded(TournamentEndedEvent tournamentEndedEvent);

    void onPlayerRegistered(PlayerRegistered playerRegistered);

    void onInvalidPlayerName(InvalidPlayerName invalidPlayerName);

    void onGameLink(GameLinkEvent gameLinkEvent);

    String getServerHost();

    int getServerPort();

    void onConnected();

    void onSessionClosed();

    String getName();

    GameMode getGameMode();

}
