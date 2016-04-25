package se.cygni.snake.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.cygni.snake.api.event.MapUpdateEvent;

import java.util.List;

@RestController
public class GameHistoryController {

    private final GameHistoryStorage storage;

    @Autowired
    public GameHistoryController(GameHistoryStorage storage) {
        this.storage = storage;
    }

    @RequestMapping("/history")
    public List<MapUpdateEvent> authenticate(
            @RequestParam(value="gameId") String gameId) {

        return storage.getAllMapUpdatesForGame(gameId);
    }
}
