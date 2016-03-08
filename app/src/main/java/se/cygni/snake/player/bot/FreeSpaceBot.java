package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;

public class FreeSpaceBot extends BotPlayer {
    public FreeSpaceBot(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
    }
}
