package se.cygni.snake.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.cygni.snake.api.GameMessage;

import java.util.List;

@RestController
public class GameHistoryController {

    private final GameHistoryStorage storage;

    @Autowired
    public GameHistoryController(GameHistoryStorage storage) {
        this.storage = storage;
    }

    @RequestMapping(value = "/history/{gameId}", method = RequestMethod.GET)
    public List<GameMessage> getGame(
                @PathVariable("gameId") String gameId) {

        return storage.getAllMessagesForGame(gameId);
    }

    @RequestMapping(value = "/history/search/{name}", method = RequestMethod.GET)
    public List<String> searchGame(
            @PathVariable("name") String name) {

        return storage.listGamesWithPlayer(name);
    }
}
