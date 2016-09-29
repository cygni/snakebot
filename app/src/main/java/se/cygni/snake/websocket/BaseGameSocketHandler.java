package se.cygni.snake.websocket;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.exception.InvalidMessage;
import se.cygni.snake.api.request.HeartBeatRequest;
import se.cygni.snake.api.response.HeartBeatResponse;

import java.io.IOException;
import java.util.UUID;

public abstract class BaseGameSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(BaseGameSocketHandler.class);

    private final String playerId;
    private EventBus outgoingEventBus;
    private EventBus incomingEventBus;
    private WebSocketSession webSocketSession;

    public BaseGameSocketHandler() {

        // Create a playerId for this player
        playerId = UUID.randomUUID().toString();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.webSocketSession = session;
    }

    private void sendHeartbeat() {
        HeartBeatResponse heartBeatResponse = new HeartBeatResponse();
        try {
            heartBeatResponse.setReceivingPlayerId(playerId);
            sendSnakeMessage(heartBeatResponse);
        } catch (Exception e) {
            log.error("Failed to send heartbeat response", e);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.trace("Received: {}", message.getPayload());

        try {
            // Deserialize message
            GameMessage gameMessage = GameMessageParser.decodeMessage(message.getPayload());

            // Overwrite playerId to hinder any cheating
            gameMessage.setReceivingPlayerId(playerId);

            if (gameMessage instanceof HeartBeatRequest) {
                sendHeartbeat();
                return;
            }

            // Send to game
            incomingEventBus.post(gameMessage);
        } catch (Throwable e) {
            log.error("Could not handle incoming text message: {}", e.getMessage());

            InvalidMessage invalidMessage = new InvalidMessage(
                    "Could not understand this message. Error:" + e.getMessage(),
                    message.getPayload()
            );
            invalidMessage.setReceivingPlayerId(playerId);

            try {
                log.info("Sending InvalidMessage to client.");
                outgoingEventBus.post(invalidMessage);
            } catch (Throwable ee) {
                ee.printStackTrace();
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("handleTransportError", exception);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        session.close(CloseStatus.SERVER_ERROR);
        outgoingEventBus.unregister(this);
        playerLostConnection();

        log.info("afterConnectionClosed {}", status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    @Subscribe
    public void sendSnakeMessage(GameMessage message) throws IOException {

        // Verify that this message is intended to this player (or null if for all players)
        if (!StringUtils.isEmpty(message.getReceivingPlayerId()) && !playerId.equals(message.getReceivingPlayerId())) {
            return;
        }
        try {
            String msg = GameMessageParser.encodeMessage(message);
            log.trace("Sending: {}", msg);
            webSocketSession.sendMessage(new TextMessage(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void playerLostConnection();

    protected String getPlayerId() {
        return playerId;
    }

    protected void setOutgoingEventBus(EventBus eventBus) {
        this.outgoingEventBus = eventBus;
        outgoingEventBus.register(this);
    }

    protected void setIncomingEventBus(EventBus eventBus) {
        this.incomingEventBus = eventBus;
    }
}
