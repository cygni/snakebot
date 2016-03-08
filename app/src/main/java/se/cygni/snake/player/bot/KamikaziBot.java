package se.cygni.snake.player.bot;

import com.google.common.eventbus.EventBus;

public class KamikaziBot extends BotPlayer {

    public KamikaziBot(String playerId, EventBus incomingEventbus) {
        super(playerId, incomingEventbus);
    }
}
