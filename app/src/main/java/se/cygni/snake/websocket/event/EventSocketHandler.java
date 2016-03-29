package se.cygni.snake.websocket.event;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.event.GameAbortedEvent;
import se.cygni.snake.api.event.GameChangedEvent;
import se.cygni.snake.api.event.GameCreatedEvent;
import se.cygni.snake.api.event.GameEndedEvent;
import se.cygni.snake.apiconversion.GameSettingsConverter;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.game.Game;
import se.cygni.snake.game.GameManager;
import se.cygni.snake.websocket.event.api.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a per-connection websocket. That means a new instance will
 * be created for each connecting client.
 */
public class EventSocketHandler extends TextWebSocketHandler {

    private static Logger log = LoggerFactory.getLogger(EventSocketHandler.class);

    private WebSocketSession session;
    private String[] filterGameIds = new String[0];
    private EventBus globalEventBus;
    private GameManager gameManager;

    @Autowired
    public EventSocketHandler(EventBus globalEventBus, GameManager gameManager) {
        this.globalEventBus = globalEventBus;
        this.gameManager = gameManager;
        log.info("EventSocketHandler started!");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Opened new event session for " + session.getId());
        this.session = session;
        globalEventBus.register(this);
        sendListOfActiveGames();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        globalEventBus.unregister(this);
        log.info("Removed session: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        log.debug(message.getPayload());
        ApiMessage apiMessage = ApiMessageParser.decodeMessage(message.getPayload());

        if (apiMessage instanceof ListActiveGames) {
            sendListOfActiveGames();
        } else if (apiMessage instanceof SetGameFilter) {
            setActiveGameFilter((SetGameFilter)apiMessage);
        } else if (apiMessage instanceof StartGame) {
            startGame((StartGame)apiMessage);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws Exception {

        session.close(CloseStatus.SERVER_ERROR);
        globalEventBus.unregister(this);
        log.info("Transport error, removed session: {}", session.getId());
    }

    @Subscribe
    public void onInternalGameEvent(InternalGameEvent event) {

        log.info("EventSocketHandler got a message: " + event.getGameMessage().getType());
        GameMessage gameMessage = event.getGameMessage();

        if (gameMessage instanceof GameCreatedEvent ||
                gameMessage instanceof GameChangedEvent ||
                gameMessage instanceof GameAbortedEvent) {
            sendListOfActiveGames();
            return;
        }

        if (gameMessage instanceof GameEndedEvent) {
            sendListOfActiveGames();
        }

        sendEvent(event.getGameMessage());
    }

    private void sendEvent(GameMessage message) {
        if (!session.isOpen())
            return;

        try {
            String gameId = org.apache.commons.beanutils.BeanUtils.getProperty(message, "gameId");
            if (ArrayUtils.contains(filterGameIds, gameId)) {
                session.sendMessage(new TextMessage(GameMessageParser.encodeMessage(message)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendListOfActiveGames() {
//        List<Game> games = gameManager.listActiveGames();
        log.info("Seding updated list of games");
        List<Game> games = gameManager.listAllGames();

        List<ActiveGame> activeGames = games.stream().map(game -> {
            List<ActiveGamePlayer> players = game.getPlayers().stream().map( player -> {
                return new ActiveGamePlayer(player.getName(), player.getPlayerId());
            }).collect(Collectors.toList());

            return new ActiveGame(
                    game.getGameId(),
                    ArrayUtils.contains(filterGameIds, game.getGameId()),
                    GameSettingsConverter.toGameSettings(game.getGameFeatures()),
                    players);

        }).collect(Collectors.toList());

        ActiveGamesList gamesList = new ActiveGamesList(activeGames);
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(ApiMessageParser.encodeMessage(gamesList)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ActiveGamesList gamesList = new ActiveGamesList(gameManager.listGameIds());
//        try {
//            if (session.isOpen()) {
//                session.sendMessage(new TextMessage(ApiMessageParser.encodeMessage(gamesList)));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void setActiveGameFilter(SetGameFilter gameFilter) {
        this.filterGameIds = gameFilter.getIncludedGameIds();
    }

    private void startGame(StartGame apiMessage) {
        Game game = gameManager.getGame(apiMessage.getGameId());
        log.info(apiMessage.getGameId());
        if (game != null) {
            log.info("Starting game: {}", game.getGameId());
            log.info("Active remote players: {}", game.getLiveAndRemotePlayers().size());
            game.startGame();
        }
    }
}
