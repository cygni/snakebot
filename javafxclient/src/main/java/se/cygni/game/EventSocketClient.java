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
import se.cygni.snake.api.response.PlayerRegistered;
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.ApiMessageParser;
import se.cygni.snake.eventapi.request.SetGameFilter;
import se.cygni.snake.eventapi.request.StartGame;
import se.cygni.snake.eventapi.response.ActiveGamesList;
import se.cygni.snake.eventapi.response.InternalPong;

public class EventSocketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSocketClient.class);

    private String url = "ws://localhost:8080/events";
    private EventListener listener;
    private WebSocketSession apiSocketSession;
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

    private void sendApiMesssage(ApiMessage message) {
        try {
            String msg = ApiMessageParser.encodeMessage(message);
            TextMessage textMessage = new TextMessage(msg);
            apiSocketSession.sendMessage(textMessage);
        } catch (Exception e) {
            LOGGER.error("Error sending api message", e);
        }
    }

    private boolean tryToHandleGameMessage(String msg) {
        try {
            GameMessage gameMessage = GameMessageParser.decodeMessage(msg);

            switch (gameMessage.getSimpleType()) {
                case "MapUpdateEvent":
                    listener.onMapUpdate((MapUpdateEvent) gameMessage);
                    break;
                case "SnakeDeadEvent":
                    listener.onSnakeDead((SnakeDeadEvent) gameMessage);
                    break;
                case "GameEndedEvent":
                    listener.onGameEnded((GameEndedEvent) gameMessage);
                    break;
                case "GameStartingEvent":
                    listener.onGameStarting((GameStartingEvent) gameMessage);
                    break;
                case "PlayerRegistered":
                    listener.onPlayerRegistered((PlayerRegistered) gameMessage);
                    break;
                case "InvalidPlayerName":
                    listener.onInvalidPlayerName((InvalidPlayerName) gameMessage);
                    break;
                case "PlayerPong":
                    LOGGER.info("Received PONG for player {}!", gameMessage.getReceivingPlayerId());
                    break;
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

    public void connect() {
        WebSocketClient wsClient = new StandardWebSocketClient();

        wsClient.doHandshake(new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                System.out.println("connected");
                apiSocketSession = session;
                sendHeartBeat();
            }

            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                msgBuffer.append(message.getPayload());

                if (!message.isLast()) {
                    return;
                }

                String msgPayload = msgBuffer.toString();
                msgBuffer = new StringBuilder();

                listener.onMessage(msgPayload);

                try {
                    ApiMessage apiMessage = ApiMessageParser.decodeMessage(msgPayload);
                    if (apiMessage instanceof InternalPong) {
                        LOGGER.info("Heartbeat received...");
                        sendHeartBeat();
                    }

                } catch (Exception e) {

                }

                if (!tryToHandleGameMessage(msgPayload)) {
                    tryToHandleApiMessage(msgPayload);
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                LOGGER.info("transport error ");
                exception.printStackTrace();
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                LOGGER.info("connection closed");
            }

            @Override
            public boolean supportsPartialMessages() {
                return true;
            }
        }, url);
    }

    private void sendHeartBeat() {
        HeartbeatSender heartbeatSender = new HeartbeatSender(apiSocketSession);
        Thread thread = new Thread(heartbeatSender);
        thread.start();
    }
}
