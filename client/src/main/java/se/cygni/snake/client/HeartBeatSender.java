package se.cygni.snake.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.request.HeartBeatRequest;

/**
 * @author Alan Tibbetts
 * @since 14/04/16
 */
public class HeartBeatSender implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HeartBeatSender.class);

    private static final int DEFAULT_HEARTBEAT_PERIOD_IN_SECONDS = 30;

    private final WebSocketSession session;
    private final String playerId;

    public HeartBeatSender(final WebSocketSession session, final String playerId) {
        this.session = session;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(DEFAULT_HEARTBEAT_PERIOD_IN_SECONDS * 1000);
        } catch (InterruptedException e) {
            log.error("Heartbeat sleep period interrupted", e);
        }

        sendHeartbeat();
    }

    private void sendHeartbeat() {
        try {
            log.trace("Sending heartbeat");
            HeartBeatRequest heartBeatRequest = new HeartBeatRequest();
            heartBeatRequest.setReceivingPlayerId(playerId);
            session.sendMessage(new TextMessage(GameMessageParser.encodeMessage(heartBeatRequest)));
        } catch (Exception e) {
            log.error("Failed to send heartbeat over websocket", e);
        }
    }
}
