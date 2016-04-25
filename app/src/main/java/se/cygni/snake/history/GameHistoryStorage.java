package se.cygni.snake.history;

import se.cygni.snake.api.event.MapUpdateEvent;

import java.util.List;

public interface GameHistoryStorage {

    void addToStorage(MapUpdateEvent mapUpdateEvent);

    List<MapUpdateEvent> getAllMapUpdatesForGame(String gameId);
    List<String> listGamesWithPlayer(String playerName);



}
