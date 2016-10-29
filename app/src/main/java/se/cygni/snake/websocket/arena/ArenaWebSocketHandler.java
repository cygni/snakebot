package se.cygni.snake.websocket.arena;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.cygni.snake.arena.ArenaManager;
import se.cygni.snake.websocket.BaseGameSocketHandler;

public class ArenaWebSocketHandler extends BaseGameSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ArenaWebSocketHandler.class);

    private final ArenaManager arenaManager;

    @Autowired
    public ArenaWebSocketHandler(ArenaManager arenaManager) {
        // TODO one arena-manager per arena??

        log.info("Started arena web socket handler");
        this.arenaManager = arenaManager;

        setOutgoingEventBus(arenaManager.getOutgoingEventBus());
        setIncomingEventBus(arenaManager.getIncomingEventBus());
    }


    @Override
    protected void playerLostConnection() {
        log.info("{} lost connection", getPlayerId());
        arenaManager.playerLostConnection(getPlayerId());
    }
}
