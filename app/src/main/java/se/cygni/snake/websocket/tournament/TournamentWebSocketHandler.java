package se.cygni.snake.websocket.tournament;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se.cygni.snake.game.TournamentManager;

public class TournamentWebSocketHandler extends TextWebSocketHandler {

    private static Logger logger = LoggerFactory.getLogger(TournamentWebSocketHandler.class);

    private TournamentManager tournamentManager;

    @Autowired
    public TournamentWebSocketHandler(TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.debug("Opened new session in instance " + this);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        logger.debug(message.getPayload());
        session.sendMessage(new TextMessage(message.getPayload()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
    }
}
