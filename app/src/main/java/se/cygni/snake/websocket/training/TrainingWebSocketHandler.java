package se.cygni.snake.websocket.training;


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
import se.cygni.snake.game.Game;
import se.cygni.snake.game.GameManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class TrainingWebSocketHandler extends TextWebSocketHandler {

    private static Logger log = LoggerFactory.getLogger(TrainingWebSocketHandler.class);

    private GameManager gameManager;

    private WebSocketSession webSocketSession;
    private final EventBus outgoingEventBus;
    private final EventBus incomingEventBus;
    private final Game game;
    private final String playerId;

    @Autowired
    public TrainingWebSocketHandler(GameManager gameManager) {
        this.gameManager = gameManager;
        log.info("Started training web socket handler");

        // Create a playerId for this player
        playerId = UUID.randomUUID().toString();

        game = gameManager.createTrainingGame();

        // Get an eventbus and register this handler
        outgoingEventBus = game.getOutgoingEventBus();
        outgoingEventBus.register(this);

        incomingEventBus = game.getIncomingEventBus();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        log.debug("Opened new session in instance " + this);
        this.webSocketSession = session;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        log.debug(message.getPayload());

        // Deserialize message
        GameMessage gameMessage = GameMessageParser.decodeMessage(message.getPayload());

        // Overwrite playerId to hinder any cheating
        gameMessage.setReceivingPlayerId(playerId);

        // Send to game
        incomingEventBus.post(gameMessage);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws Exception {

        log.error("handleTransportError", exception);
        session.close(CloseStatus.SERVER_ERROR);
        outgoingEventBus.unregister(this);
        game.playerLostConnection(playerId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        session.close(CloseStatus.SERVER_ERROR);
        outgoingEventBus.unregister(this);
        game.playerLostConnection(playerId);
        game.abort();
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
        webSocketSession.sendMessage(new TextMessage(GameMessageParser.encodeMessage(message)));
    }

    public static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        log.debug("Original: " + data.length / 1024 + " Kb");
        log.debug("Compressed: " + output.length / 1024 + " Kb");
        return output;
    }
    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        log.debug("Original: " + data.length);
        log.debug("Compressed: " + output.length);
        return output;
    }
}
