package se.cygni.game;

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
import se.cygni.snake.api.response.HeartBeatResponse;
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.client.HeartBeatSender;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.ApiMessageParser;
import se.cygni.snake.eventapi.request.SetGameFilter;
import se.cygni.snake.eventapi.request.StartGame;
import se.cygni.snake.eventapi.response.ActiveGamesList;

public class EventSocketClient {

    private static final Logger log = LoggerFactory.getLogger(EventSocketClient.class);

    private String url = "ws://localhost:8080/events";
    private EventListener listener;
    private WebSocketSession webSocketSession;
    private StringBuilder msgBuffer = new StringBuilder();

    public EventSocketClient(String url, EventListener listener) {
        this.url = url;
        this.listener = listener;
    }

    public void setGameIdFilter(String... ids) {
        SetGameFilter setGameFilter = new SetGameFilter(ids);
        sendApiMesssage(setGameFilter);
    }

    public void startGame(String gameId) {
        StartGame startGame = new StartGame(gameId);
        sendApiMesssage(startGame);
    }

    public void sendApiMesssage(ApiMessage message) {
        try {
            String msg = ApiMessageParser.encodeMessage(message);
            TextMessage textMessage = new TextMessage(msg);
            webSocketSession.sendMessage(textMessage);
        } catch (Exception e) {
            log.error("Error sending api message", e);
        }
    }

    private boolean tryToHandleGameMessage(String msg) {
        try {
            GameMessage gameMessage = GameMessageParser.decodeMessage(msg);

            if (gameMessage instanceof MapUpdateEvent) {
                listener.onMapUpdate((MapUpdateEvent) gameMessage);
            } else if (gameMessage instanceof SnakeDeadEvent) {
                listener.onSnakeDead((SnakeDeadEvent) gameMessage);
            } else if (gameMessage instanceof GameEndedEvent) {
                listener.onGameEnded((GameEndedEvent) gameMessage);
            } else if (gameMessage instanceof GameStartingEvent) {
                listener.onGameStarting((GameStartingEvent) gameMessage);
            } else if (gameMessage instanceof PlayerRegistered) {
                listener.onPlayerRegistered((PlayerRegistered) gameMessage);
            } else if (gameMessage instanceof InvalidPlayerName) {
                listener.onInvalidPlayerName((InvalidPlayerName) gameMessage);
            } else if (gameMessage instanceof HeartBeatResponse) {
                sendHeartbeat();
            }

            return true;
        } catch (Exception e) {
        }
        return false;
    }

    private boolean tryToHandleApiMessage(String msg) {
        try {
            ApiMessage apiMessage = ApiMessageParser.decodeMessage(msg);

            if (apiMessage instanceof ActiveGamesList) {
                listener.onActiveGamesList((ActiveGamesList) apiMessage);
            }

            return true;
        } catch (Exception e) {
        }
        return false;
    }

    private void sendHeartbeat() {
        HeartBeatSender heartbeatSender = new HeartBeatSender(webSocketSession, null);
        Thread thread = new Thread(heartbeatSender);
        thread.start();
    }

    public void connect() {
        WebSocketClient wsClient = new StandardWebSocketClient();

        wsClient.doHandshake(new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                System.out.println("connected");
                webSocketSession = session;
                sendHeartbeat();
            }

            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                msgBuffer.append(message.getPayload());

                if (!message.isLast()) {
                    return;
                }

                String msgPayload = msgBuffer.toString();
                msgBuffer = new StringBuilder();

                System.out.println("Received: " + msgPayload);
                listener.onMessage(msgPayload);

                if (!tryToHandleGameMessage(msgPayload)) {
                    tryToHandleApiMessage(msgPayload);
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                log.info("transport error ");
                exception.printStackTrace();
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.info("connection closed");
            }

            @Override
            public boolean supportsPartialMessages() {
                return true;
            }
        }, url);
    }

}
