package se.cygni.snake.persistence.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.cygni.snake.eventapi.history.GameHistory;
import se.cygni.snake.eventapi.history.GameHistorySearchResult;

import java.util.Optional;

@RestController
public class GameHistoryController {

    private static Logger log = LoggerFactory
            .getLogger(GameHistoryController.class);

    private final GameHistoryStorage storage;

    @Autowired
    public GameHistoryController(GameHistoryStorage storage) {
        this.storage = storage;
    }

    @GetMapping("/history/{gameId}")
    public ResponseEntity<GameHistory> getGame(
            @PathVariable("gameId") String gameId) {

        Optional<GameHistory> gameHistory = storage.getGameHistory(gameId);
        return gameHistory
                .map(gameHistory1 -> new ResponseEntity<>(gameHistory1, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/history/search/{name}")
    public ResponseEntity<GameHistorySearchResult> searchGame(
            @PathVariable("name") String name) {

        try {
            GameHistorySearchResult result = storage.listGamesWithPlayer(name);
            if (result.getItems().size() == 0) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<GameHistorySearchResult>(result, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("Failed to search for games with player: {}", name, e);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
