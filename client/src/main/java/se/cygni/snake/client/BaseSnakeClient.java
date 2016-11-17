package se.cygni.snake.client;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.event.*;
import se.cygni.snake.api.exception.InvalidMessage;
import se.cygni.snake.api.exception.InvalidPlayerName;
import se.cygni.snake.api.model.GameMode;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.request.ClientInfo;
import se.cygni.snake.api.request.RegisterMove;
import se.cygni.snake.api.request.RegisterPlayer;
import se.cygni.snake.api.request.StartGame;
import se.cygni.snake.api.response.HeartBeatResponse;
import se.cygni.snake.api.response.PlayerRegistered;

import javax.websocket.OnError;
import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class BaseSnakeClient extends TextWebSocketHandler implements SnakeClient {

    private static final Logger log = LoggerFactory.getLogger(BaseSnakeClient.class);

    private WebSocketSession session;

    private String playerId;
    private String lastGameId;
    private boolean gameEnded = false;
    private boolean tournamentEnded = false;

    private String arenaName = null;

    public void registerForGame(GameSettings gameSettings) {
        log.info("Register for game...");
        RegisterPlayer registerPlayer = new RegisterPlayer(getName(), gameSettings);
        sendMessage(registerPlayer);
    }

    public void startGame() {
        log.info("Starting game...");
        StartGame startGame = new StartGame();
        startGame.setReceivingPlayerId(playerId);
        sendMessage(startGame);
    }

    public void registerMove(long gameTick, SnakeDirection direction) {
        RegisterMove registerMove = new RegisterMove(lastGameId, gameTick, direction);
        registerMove.setReceivingPlayerId(playerId);
        sendMessage(registerMove);
    }

    public void sendClientInfo() {
        String clientVersion = readVersionFromPropertiesFile();

        ClientInfo clientInfo = new ClientInfo(
                "Java",
                SystemUtils.JAVA_VERSION,
                SystemUtils.OS_NAME,
                SystemUtils.OS_VERSION,
                clientVersion);

        sendMessage(clientInfo);
    }

    private String readVersionFromPropertiesFile() {
        String version = "Unknown";
        try (BufferedReader bis = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("client.properties")))) {
            String line = null;
            while ((line = bis.readLine()) != null) {
                if (line.trim().length() > 0) {
                    String[] split = line.split("=");
                    if (split[0].equals("client.version")) {
                        version = split[1];
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to load properties file, could not determine client version");
        }
        return version;
    }

    public boolean isPlaying() {
        if (getGameMode() == GameMode.TRAINING) {
            return session != null && !gameEnded;
        } else {
            return session != null && !tournamentEnded;
        }
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

    public ListenableFuture<WebSocketSession> connect() {
        String arenaSuffix = getGameMode() == GameMode.ARENA && arenaName != null ? "/"+arenaName : "";
        String uri = String.format("ws://%s:%d/%s%s", getServerHost(), getServerPort(), getGameMode().toString().toLowerCase(), arenaSuffix);
        log.info("Connecting to {}", uri);

        WebSocketClient wsClient = new StandardWebSocketClient();
        return wsClient.doHandshake(this, uri);
    }

    private void sendHeartbeat() {
        HeartBeatSender heartbeatSender = new HeartBeatSender(session, playerId);
        Thread thread = new Thread(heartbeatSender);
        thread.start();
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
        sendHeartbeat();
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

        try {
            // Deserialize message
            GameMessage gameMessage = GameMessageParser.decodeMessage(messageRaw);
            log.debug(messageRaw);

            if (gameMessage instanceof PlayerRegistered) {
                this.onPlayerRegistered((PlayerRegistered) gameMessage);
                this.playerId = gameMessage.getReceivingPlayerId();
                sendClientInfo();
            }

            if (gameMessage instanceof MapUpdateEvent) {
                MapUpdateEvent mue = (MapUpdateEvent) gameMessage;
                this.lastGameId = mue.getGameId();
                this.onMapUpdate(mue);
            }
            if (gameMessage instanceof GameStartingEvent)
                this.onGameStarting((GameStartingEvent) gameMessage);

            if (gameMessage instanceof SnakeDeadEvent)
                this.onSnakeDead((SnakeDeadEvent) gameMessage);

            if (gameMessage instanceof GameResultEvent) {
                this.onGameResult((GameResultEvent) gameMessage);
            }

            if (gameMessage instanceof GameEndedEvent) {
                this.onGameEnded((GameEndedEvent) gameMessage);
                gameEnded = true;
            }

            if (gameMessage instanceof TournamentEndedEvent) {
                this.onTournamentEnded((TournamentEndedEvent)gameMessage);
                tournamentEnded = true;
            }

            if (gameMessage instanceof InvalidPlayerName) {
                this.onInvalidPlayerName((InvalidPlayerName) gameMessage);
            }

            if (gameMessage instanceof GameLinkEvent) {
                this.onGameLink((GameLinkEvent)gameMessage);
            }

            if (gameMessage instanceof HeartBeatResponse) {
                this.sendHeartbeat();
            }

            if (gameMessage instanceof InvalidMessage) {
                InvalidMessage invalidMessage = (InvalidMessage) gameMessage;

                log.error("Server did not understand my last message");
                log.error("Message sent: " + invalidMessage.getReceivedMessage());
                log.error("Error message: " + invalidMessage.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Could not understand received message from server: {}", messageRaw, e);
        }
    }

    @OnError
    public void onError(Throwable exception, Session session) {
        log.error("Websocket error", exception);
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

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }
}
