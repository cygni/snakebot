package se.cygni.snake.game;

import com.google.common.eventbus.EventBus;

public class TestPlayer { //implements IPlayer {

    private final String name;
    private final String playerId;
    private final Game game;
    private final EventBus outgoingEventBus;

    public TestPlayer(String name, String playerId, Game game) {
        this.name = name;
        this.playerId = playerId;
        this.game = game;
        this.outgoingEventBus = game.getOutgoingEventBus();
    }

}
