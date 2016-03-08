package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;

public class EdgeBot extends BotPlayer {

    public EdgeBot(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
    }
}
