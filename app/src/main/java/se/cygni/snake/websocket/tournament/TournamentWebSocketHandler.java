package se.cygni.snake.websocket.tournament;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.exception.InvalidMessage;
import se.cygni.snake.tournament.TournamentManager;

import java.io.IOException;
import java.util.UUID;

public class TournamentWebSocketHandler extends TextWebSocketHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(TournamentWebSocketHandler.class);

    private final EventBus outgoingEventBus;
    private final EventBus incomingEventBus;
    private TournamentManager tournamentManager;
    private final String playerId;
    private WebSocketSession webSocketSession;

    @Autowired
    public TournamentWebSocketHandler(TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;

        LOGGER.info("Started training web socket handler");

        // Create a playerId for this player
        playerId = UUID.randomUUID().toString();

        // Get an eventbus and register this handler
        outgoingEventBus = tournamentManager.getOutgoingEventBus();
        outgoingEventBus.register(this);

        incomingEventBus = tournamentManager.getIncomingEventBus();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        outgoingEventBus.unregister(this);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LOGGER.debug("Opened new session in instance " + this);
        this.webSocketSession = session;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        LOGGER.debug(message.getPayload());

        try {
            // Deserialize message
            GameMessage gameMessage = GameMessageParser.decodeMessage(message.getPayload());

            // Overwrite playerId to hinder any cheating
            gameMessage.setReceivingPlayerId(playerId);

            // Send to game
            incomingEventBus.post(gameMessage);
        } catch (Throwable e) {
            LOGGER.error("Could not handle incoming text message: {}", e.getMessage());

            InvalidMessage invalidMessage = new InvalidMessage(
                    "Could not understand this message. Error:" + e.getMessage(),
                    message.getPayload()
            );
            invalidMessage.setReceivingPlayerId(playerId);

            try {
                LOGGER.info("Sending InvalidMessage to client.");
                outgoingEventBus.post(invalidMessage);
            } catch (Throwable ee) {
                ee.printStackTrace();
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
        outgoingEventBus.unregister(this);
    }

    @Subscribe
    public void sendSnakeMessage(GameMessage message) throws IOException {

        // Verify that this message is intended to this player (or null if for all players)
        if (!StringUtils.isEmpty(message.getReceivingPlayerId()) && !playerId.equals(message.getReceivingPlayerId())) {
            return;
        }
        try {
            String msg = GameMessageParser.encodeMessage(message);
            LOGGER.debug("Sending: {}", msg);
            webSocketSession.sendMessage(new TextMessage(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
