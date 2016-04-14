package se.cygni.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import se.cygni.snake.eventapi.ApiMessageParser;
import se.cygni.snake.eventapi.request.InternalPing;

/**
 * @author Alan Tibbetts
 * @since 14/04/16
 */
public class HeartbeatSender implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatSender.class);

    private static final int DEFAULT_HEARTBEAT_PERIOD_IN_SECONDS = 30;

    private final WebSocketSession session;

    public HeartbeatSender(final WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(DEFAULT_HEARTBEAT_PERIOD_IN_SECONDS * 1000);
        } catch (InterruptedException e) {
            LOGGER.error("Heartbeat sleep period interrupted", e);
        }

        sendHeartbeat();
    }

    private void sendHeartbeat() {
        try {
            InternalPing internalPing = new InternalPing();
            session.sendMessage(new TextMessage(ApiMessageParser.encodeMessage(internalPing)));
        } catch (Exception e) {
            LOGGER.error("Failed to send message over websocket", e);
        }
    }
}
