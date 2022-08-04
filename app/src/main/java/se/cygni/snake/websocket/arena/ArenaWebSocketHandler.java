package se.cygni.snake.websocket.arena;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;
import se.cygni.snake.api.exception.InvalidArenaName;
import se.cygni.snake.api.exception.InvalidMessage;
import se.cygni.snake.arena.ArenaManager;
import se.cygni.snake.arena.ArenaSelectionManager;
import se.cygni.snake.websocket.BaseGameSocketHandler;

import java.io.IOException;

public class ArenaWebSocketHandler extends BaseGameSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(ArenaWebSocketHandler.class);

    private static final String ARENA_NAME_WHITELIST = "[a-zA-Z0-9-_]+";

    private final ArenaSelectionManager arenaSelectionManager;
    private ArenaManager arenaManager;

    @Autowired
    public ArenaWebSocketHandler(ArenaSelectionManager arenaSelectionManager) {
        this.arenaSelectionManager = arenaSelectionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        super.afterConnectionEstablished(session);

        String uri = session.getUri().getPath();

        String arenaName = uri.replaceAll(".*/", "");

        this.arenaManager = arenaSelectionManager.getArena(arenaName);
        if (this.arenaManager == null) {
            // TODO: Fix exceptions occuring since eventbus is null after this
            handleInvalidArenaName(session);
            return;
        }

        setOutgoingEventBus(arenaManager.getOutgoingEventBus());
        setIncomingEventBus(arenaManager.getIncomingEventBus());
        log.info("Started arena web socket handler");
    }

    private void handleInvalidArenaName(WebSocketSession session) {
        InvalidArenaName invalidArenaName = new InvalidArenaName(InvalidArenaName.ArenaNameInvalidReason.Nonexistent);
        invalidArenaName.setReceivingPlayerId(this.getPlayerId());
        try {
            sendSnakeMessage(invalidArenaName);
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleInvalidName(String uri, String arenaFromUri) {
        try {
            sendSnakeMessage(new InvalidMessage(
                    String.format("Arena name %s does not match allowed pattern %s", arenaFromUri, ARENA_NAME_WHITELIST),
                    uri));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void playerLostConnection() {
        log.info("{} lost connection", getPlayerId());
        if (arenaManager != null) {
            arenaManager.playerLostConnection(getPlayerId());
        }
    }
}
