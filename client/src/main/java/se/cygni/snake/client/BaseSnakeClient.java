package se.cygni.snake.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.event.SnakeDeadEvent;
import se.cygni.snake.api.exception.InvalidMessage;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.api.request.RegisterPlayer;
import se.cygni.snake.api.request.StartGame;
import se.cygni.snake.api.response.PlayerRegistered;

import java.io.IOException;

public abstract class BaseSnakeClient extends TextWebSocketHandler implements SnakeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseSnakeClient.class);

    private WebSocketSession session;

    private String playerId;
    private boolean gameEnded = false;

    public void registerForGame(GameSettings gameSettings) {
        LOGGER.info("Register for game...");
        RegisterPlayer registerPlayer = new RegisterPlayer(getName(), gameSettings);
        sendMessage(registerPlayer);
    }

    public void startGame() {
        LOGGER.info("Starting game...");
        StartGame startGame = new StartGame();
        startGame.setReceivingPlayerId(playerId);
        sendMessage(startGame);
    }

    public void registerMove(long gameTick, SnakeDirection direction) {
        RegisterMove registerMove = new RegisterMove(gameTick, direction);
        registerMove.setReceivingPlayerId(playerId);
        sendMessage(registerMove);
    }

    public boolean isPlaying() {
        return session != null && !gameEnded;
    }

    public String getPlayerId() {
        return playerId;
    }

    private void disconnect() {
        LOGGER.info("Disconnecting from server");
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                LOGGER.warn("Failed to close websocket connection");
            } finally {
                session = null;
            }
        }
    }

    public void connect() {
        WebSocketClient wsClient = new StandardWebSocketClient();
        String uri = String.format("ws://%s:%d/%s", getServerHost(), getServerPort(), getGameMode().toString().toLowerCase());
        LOGGER.info("Connecting to {}", uri);
        wsClient.doHandshake(this, uri);
    }

    private void sendMessage(GameMessage message) {

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Sending: {}", GameMessageParser.encodeMessage(message));
            }

            session.sendMessage(new TextMessage(GameMessageParser.encodeMessage(message)));
        } catch (Exception e) {
            LOGGER.error("Failed to send message over websocket", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOGGER.info("Connected to server");
        this.session = session;
        this.onConnected();
    }

    private StringBuilder msgBuffer = new StringBuilder();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        msgBuffer.append(message.getPayload());

        if (!message.isLast()) {
            return;
        }

        String messageRaw = msgBuffer.toString();
        msgBuffer = new StringBuilder();

        LOGGER.debug("Incoming message: {}", messageRaw);
        try {
            // Deserialize message
            GameMessage gameMessage = GameMessageParser.decodeMessage(messageRaw);

            switch (gameMessage.getSimpleType()) {
                case "PlayerRegistered":
                    this.onPlayerRegistered((PlayerRegistered) gameMessage);
                    this.playerId = gameMessage.getReceivingPlayerId();
                    sendHeartBeat();
                    break;

                case "MapUpdateEvent":
                    this.onMapUpdate((MapUpdateEvent) gameMessage);
                    break;

                case "GameStartingEvent":
                    this.onGameStarting((GameStartingEvent) gameMessage);
                    break;

                case "SnakeDeadEvent":
                    this.onSnakeDead((SnakeDeadEvent) gameMessage);
                    break;

                case "GameEndedEvent":
                    this.onGameEnded((GameEndedEvent) gameMessage);
                    gameEnded = true;
                    break;

                case "InvalidPlayerName":
                    this.onInvalidPlayerName((InvalidPlayerName) gameMessage);
                    break;

                case "InvalidMessage":
                    InvalidMessage invalidMessage = (InvalidMessage)gameMessage;

                    LOGGER.error("Server did not understand my last message");
                    LOGGER.error("Message sent: " + invalidMessage.getReceivedMessage());
                    LOGGER.error("Error message: " + invalidMessage.getErrorMessage());

                    break;

                case "PlayerPong":
                    LOGGER.info("Heartbeat received...");
                    sendHeartBeat();
                    break;

                default:
                    LOGGER.warn("Don't know how to process a message with type: {}", gameMessage.getType());
            }

        } catch (Exception e) {
            LOGGER.error("Could not understand received message from server: {}", messageRaw, e);
        }
    }

    private void sendHeartBeat() {
        HeartbeatSender heartbeatSender = new HeartbeatSender(session, playerId);
        Thread thread = new Thread(heartbeatSender);
        thread.start();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        LOGGER.warn("Transport error", exception);
        disconnect();
        onSessionClosed();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        LOGGER.warn("Server connection closed");
        disconnect();
        onSessionClosed();
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

}
