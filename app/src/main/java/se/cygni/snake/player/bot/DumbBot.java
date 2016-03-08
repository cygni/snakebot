package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;

public class DumbBot extends BotPlayer {
    public DumbBot(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
    }
}
