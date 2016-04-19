package se.cygni.snake.websocket.tournament;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.cygni.snake.tournament.TournamentManager;
import se.cygni.snake.websocket.BaseGameSocketHandler;

public class TournamentWebSocketHandler extends BaseGameSocketHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(TournamentWebSocketHandler.class);

    private TournamentManager tournamentManager;

    @Autowired
    public TournamentWebSocketHandler(TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;

        LOGGER.info("Started tournament web socket handler");

        // Get an eventbus and register this handler
        this.setOutgoingEventBus(tournamentManager.getOutgoingEventBus());
        this.setIncomingEventBus(tournamentManager.getIncomingEventBus());
    }

    @Override
    protected void playerLostConnection() {
        tournamentManager.playerLostConnection(getPlayerId());
    }
}
