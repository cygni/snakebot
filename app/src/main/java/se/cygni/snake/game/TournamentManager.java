package se.cygni.snake.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TournamentManager {

    private GameManager gameManager;

    @Autowired
    public TournamentManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
