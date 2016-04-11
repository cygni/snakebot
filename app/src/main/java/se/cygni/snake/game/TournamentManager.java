package se.cygni.snake.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TournamentManager {

    private GameManager gameManager;

    private boolean tournamentActive;
    private boolean tournamentStarted;
    private String tournamentId;
    private String tournamentName;

    private Map<String, Game> games = new HashMap<>();

    @Autowired
    public TournamentManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void killTournament() {
        tournamentActive = false;
        tournamentStarted = false;

        tournamentId = null;
        tournamentName = null;

        games.values().forEach(game -> game.abort());
    }

    public void createTournament(String name) {
        if (isTournamentActive() || isTournamentStarted())
            throw new RuntimeException("A tournament is already active");

        killTournament();

        tournamentActive = true;
        tournamentId = UUID.randomUUID().toString();
        tournamentName = name;

        games = new HashMap<>();
    }

    public boolean isTournamentActive() {
        return tournamentActive;
    }

    public boolean isTournamentStarted() {
        return tournamentStarted;
    }

    public String getTournamentId() {
        return tournamentId;
    }
}
