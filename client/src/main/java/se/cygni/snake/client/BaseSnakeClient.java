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
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.api.request.RegisterPlayer;
import se.cygni.snake.api.request.StartGame;
import se.cygni.snake.api.response.PlayerRegistered;

import java.io.IOException;

public abstract class BaseSnakeClient extends TextWebSocketHandler implements SnakeClient {

    private static Logger log = LoggerFactory
            .getLogger(BaseSnakeClient.class);

    private WebSocketSession session;

    private String playerId;
    private boolean gameEnded = false;

    public void registerForGame(GameSettings gameSettings) {
        RegisterPlayer registerPlayer = new RegisterPlayer(
                getName(),
                getColor(),
                gameSettings
        );
        sendMessage(registerPlayer);
    }

    public void startGame() {
        StartGame startGame = new StartGame();
        startGame.setReceivingPlayerId(playerId);
        log.info("Starting game");
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
        log.info("Disconnecting from server");
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                log.warn("Failed to close websocket connection");
            } finally {
                session = null;
            }
        }
    }

    public void connect() {
        WebSocketClient wsClient = new StandardWebSocketClient();
        String uri = String.format("ws://%s:%d/%s", getServerHost(), getServerPort(), getGameMode().toString());
        log.info("Connecting to {}", uri);
        wsClient.doHandshake(this, uri);
    }

    private void sendMessage(GameMessage message) {

        try {
            if (log.isDebugEnabled()) {
                log.debug("Sending: {}", GameMessageParser.encodeMessage(message));
            }
            session.sendMessage(new TextMessage(
                    GameMessageParser.encodeMessage(message)
            ));
        } catch (Exception e) {
            log.error("Failed to send message over websocket", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connected to server");
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

        // Deserialize message
        GameMessage gameMessage = GameMessageParser.decodeMessage(msgBuffer.toString());
        log.debug(msgBuffer.toString());
        msgBuffer = new StringBuilder();
        try {
            if (gameMessage instanceof PlayerRegistered) {
                this.onPlayerRegistered((PlayerRegistered) gameMessage);
                this.playerId = gameMessage.getReceivingPlayerId();
            }

            if (gameMessage instanceof MapUpdateEvent)
                this.onMapUpdate((MapUpdateEvent) gameMessage);

            if (gameMessage instanceof GameStartingEvent)
                this.onGameStarting((GameStartingEvent) gameMessage);

            if (gameMessage instanceof SnakeDeadEvent)
                this.onSnakeDead((SnakeDeadEvent) gameMessage);

            if (gameMessage instanceof GameEndedEvent) {
                this.onGameEnded((GameEndedEvent) gameMessage);
                gameEnded = true;
            }

            if (gameMessage instanceof InvalidPlayerName) {
                this.onInvalidPlayerName((InvalidPlayerName) gameMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("Transport error", exception);
        disconnect();
        onSessionClosed();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.warn("Server connection closed");
        disconnect();
        onSessionClosed();
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

}
