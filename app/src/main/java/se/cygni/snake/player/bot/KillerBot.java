package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;

public class KillerBot extends BotPlayer {

    public KillerBot(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
    }
}
