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
import se.cygni.game.WorldState;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.transformation.AddWorldObjectAtRandomPosition;
import se.cygni.game.worldobject.Food;
import se.cygni.game.worldobject.Obstacle;
import se.cygni.game.worldobject.SnakeBody;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.apiconversion.WorldStateConverter;
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

        /*
        this.session = session;
        isConnected = true;
        // Lambda Runnable
        Runnable eventGenerator = () -> {
            while (isConnected) {
                long worldTick = 0;
                try {
                    session.sendMessage(new TextMessage(GameMessageParser.encodeMessage(getRandomMapUpdateEvent(worldTick))));
                    worldTick++;
                    Thread.sleep(250);
                    log.info("World tick: {}", worldTick);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(eventGenerator).start();
        */
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
        List<Game> games = gameManager.listActiveGames();

        List<ActiveGame> activeGames = games.stream().map(game -> {
            List<ActiveGamePlayer> players = game.getPlayers().stream().map( player -> {
                return new ActiveGamePlayer(player.getName(), player.getPlayerId());
            }).collect(Collectors.toList());

            return new ActiveGame(game.getGameId(), game.getGameFeatures(), players);

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
        if (game != null) {
            log.info("Starting game: {}", game.getGameId());
            log.info("Active remote players: {}", game.getLiveAndRemotePlayers().size());
            game.startGame();
        }
    }

    private MapUpdateEvent getRandomMapUpdateEvent(long gameTick) {
        return new MapUpdateEvent(gameTick, "666", getRandomMap());
    }

    private Map getRandomMap() {
        WorldState ws = new WorldState(15, 15);

        // Snake Python
        String snakeName = "python";
        SnakeHead head = new SnakeHead(snakeName, "id_python", 101);
        SnakeBody body1 = new SnakeBody(116);
        SnakeBody body2 = new SnakeBody(115);

        head.setNextSnakePart(body1);
        body1.setNextSnakePart(body2);

        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head, head.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body1, body1.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body2, body2.getPosition());

        // Snake Cobra
        String snakeName2 = "cobra";
        SnakeHead head2 = new SnakeHead(snakeName2, "id_cobra", 109);
        SnakeBody body21 = new SnakeBody(108);
        SnakeBody body22 = new SnakeBody(123);
        SnakeBody body23 = new SnakeBody(138);

        head2.setNextSnakePart(body21);
        body21.setNextSnakePart(body22);
        body22.setNextSnakePart(body23);

        ws = SnakeTestUtil.replaceWorldObjectAt(ws, head2, head2.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body21, body21.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body22, body22.getPosition());
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, body23, body23.getPosition());

        // 10 Obstacles
        for (int x=0; x<10; x++) {
            AddWorldObjectAtRandomPosition ar = new AddWorldObjectAtRandomPosition(new Obstacle());
            ws = ar.transform(ws);
        }

        // 5 Foods
        for (int x=0; x<10; x++) {
            AddWorldObjectAtRandomPosition ar = new AddWorldObjectAtRandomPosition(new Food());
            ws = ar.transform(ws);
        }

        return WorldStateConverter.convertWorldState(ws, 1);
    }
}
