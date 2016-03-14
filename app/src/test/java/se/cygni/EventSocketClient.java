package se.cygni;

import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import se.cygni.snake.websocket.event.api.ApiMessage;
import se.cygni.snake.websocket.event.api.ApiMessageParser;
import se.cygni.snake.websocket.event.api.ListActiveGames;

import java.util.ArrayList;
import java.util.List;

public class EventSocketClient {
    public static void main(String[] args) {

        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        SockJsClient sockJsClient = new SockJsClient(transports);

        sockJsClient.doHandshake(new WebSocketHandler() {
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
                return true;
            }
        }, "ws://localhost:8080/events");

        try {
            Thread.currentThread().sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
