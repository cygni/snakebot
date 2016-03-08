package se.cygni;

import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import se.cygni.snake.websocket.event.api.*;

public class EventSocketClient {
    public static void main(String[] args) {

        WebSocketClient wsClient = new StandardWebSocketClient();

        wsClient.doHandshake(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                System.out.println("connected");
                ListActiveGames listActiveGames = new ListActiveGames();

                Runnable eventGenerator = () -> {
                    while (session.isOpen()) {
                        try {
                            session.sendMessage(new TextMessage(ApiMessageParser.encodeMessage(listActiveGames)));
                            Thread.sleep(250);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(eventGenerator).start();
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                System.out.println(message.getPayload());

                // Just start the first game
                ApiMessage apiMessage = ApiMessageParser.decodeMessage(message.getPayload().toString());
                if (apiMessage instanceof ActiveGamesList) {
                    ActiveGamesList agl = (ActiveGamesList)apiMessage;
                    if (agl.getActiveGameIds().length > 0) {
                        StartGame startGame = new StartGame(agl.getActiveGameIds()[0]);
                        session.sendMessage(new TextMessage(ApiMessageParser.encodeMessage(startGame)));
                    }
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                System.out.println("transport error ");
                exception.printStackTrace();
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                System.out.println("connection closed");
            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        }, "ws://localhost:8080/events");

        try {
            Thread.currentThread().sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
