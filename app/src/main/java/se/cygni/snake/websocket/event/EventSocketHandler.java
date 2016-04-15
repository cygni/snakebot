package se.cygni.snake.websocket.event;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.beanutils.BeanUtils;
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
import se.cygni.snake.eventapi.ApiMessage;
import se.cygni.snake.eventapi.ApiMessageParser;
import se.cygni.snake.eventapi.exception.Unauthorized;
import se.cygni.snake.eventapi.model.ActiveGame;
import se.cygni.snake.eventapi.model.ActiveGamePlayer;
import se.cygni.snake.eventapi.request.*;
import se.cygni.snake.eventapi.response.ActiveGamesList;
import se.cygni.snake.eventapi.response.TournamentCreated;
import se.cygni.snake.game.Game;
import se.cygni.snake.game.GameManager;
import se.cygni.snake.tournament.TournamentManager;
import se.cygni.snake.security.TokenService;

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
    private TournamentManager tournamentManager;
    private TokenService tokenService;

    @Autowired
    public EventSocketHandler(
            EventBus globalEventBus,
            GameManager gameManager,
            TournamentManager tournamentManager,
            TokenService tokenService) {

        this.globalEventBus = globalEventBus;
        this.gameManager = gameManager;
        this.tournamentManager = tournamentManager;
        this.tokenService = tokenService;
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

        try {
            ApiMessage apiMessage = ApiMessageParser.decodeMessage(message.getPayload());

            if (!verifyTokenSendErrorIfUnauthorized(apiMessage)) {
                return;
            }

            if (apiMessage instanceof ListActiveGames) {
                sendListOfActiveGames();

            } else if (apiMessage instanceof SetGameFilter) {
                setActiveGameFilter((SetGameFilter) apiMessage);

            } else if (apiMessage instanceof StartGame) {
                startGame((StartGame) apiMessage);

            } else if (apiMessage instanceof KillTournament) {
                // ToDo: Do we really need the current tournamentId?
                tournamentManager.killTournament();

            } else if (apiMessage instanceof CreateTournament) {
                CreateTournament createTournament = (CreateTournament)apiMessage;

                // ToDo: Handle case that a tournament is already started
                tournamentManager.createTournament(createTournament.getTournamentName());
                sendApiMessage(new TournamentCreated(
                        tournamentManager.getTournamentId(),
                        tournamentManager.getTournamentName(),
                        GameSettingsConverter.toGameSettings(
                                tournamentManager.getGameFeatures()
                        )
                ));

            } else if (apiMessage instanceof UpdateTournamentSettings) {
                UpdateTournamentSettings updateTournamentSettings = (UpdateTournamentSettings)apiMessage;

                // ToDo: Handle case that a tournament is already started
                tournamentManager.setGameFeatures(
                        GameSettingsConverter.toGameFeatures(
                                updateTournamentSettings.getGameSettings()
                        )
                );

            } else if (apiMessage instanceof StartTournamentGame) {
                StartTournamentGame startGame = (StartTournamentGame)apiMessage;

                tournamentManager.startGame(startGame.getGameId());
            }
        } catch (Exception e) {
            log.error("Failed to understand received message: {}", message.getPayload(), e);
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

        sendGameEvent(event.getGameMessage());
    }

    private void sendGameEvent(GameMessage message) {
        if (!session.isOpen())
            return;

        String gameId = extractGameId(message);
        if (ArrayUtils.contains(filterGameIds, gameId)) {
            sendGameMessage(message);
        }
    }

    private void sendListOfActiveGames() {
        log.info("Seding updated list of games");
        List<Game> games = gameManager.listAllGames();

        List<ActiveGame> activeGames = games.stream().map(game -> {
            List<ActiveGamePlayer> players = game.getPlayers().stream().map(player -> {
                return new ActiveGamePlayer(player.getName(), player.getPlayerId());
            }).collect(Collectors.toList());

            return new ActiveGame(
                    game.getGameId(),
                    ArrayUtils.contains(filterGameIds, game.getGameId()),
                    GameSettingsConverter.toGameSettings(game.getGameFeatures()),
                    players);

        }).collect(Collectors.toList());

        ActiveGamesList gamesList = new ActiveGamesList(activeGames);
        sendApiMessage(gamesList);
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

    private boolean verifyTokenSendErrorIfUnauthorized(ApiMessage apiMessage) {
        try {
            String token = BeanUtils.getProperty(apiMessage, "token");
            if (!tokenService.isTokenValid(token)) {
                String msg = String.format("Operation %s requires valid token. Specified token: %s is invalid",
                        apiMessage.getClass().getSimpleName(),
                        token);
                Unauthorized unauthorized = new Unauthorized(msg);
                sendApiMessage(unauthorized);
                return false;
            }
        } catch (Exception e) {
            // Happens if the ApiMessage doesn't have a token property
            // in which case no authorization is needed.
        }
        return true;
    }

    private String extractGameId(GameMessage gameMessage) {
        try {
            return BeanUtils.getProperty(gameMessage, "gameId");
        } catch (Exception e) {
            return ":";
        }
    }

    private void sendGameMessage(GameMessage gameMessage) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(GameMessageParser.encodeMessage(gameMessage)));
            }
        } catch (IOException e) {
            log.error("Failed to send GameMessage over eventsocket", e);
        }
    }

    private void sendApiMessage(ApiMessage apiMessage) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(ApiMessageParser.encodeMessage(apiMessage)));
            }
        } catch (IOException e) {
            log.error("Failed to send GameMessage over eventsocket", e);
        }
    }
}
