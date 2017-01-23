package se.cygni.snake.arena;


import com.google.common.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.cygni.snake.game.GameManager;

import java.util.HashMap;
import java.util.Map;

@Component
public class ArenaSelectionManager {
    public static final String OFFICIAL_ARENA_NAME = "official";
    private static final Logger log = LoggerFactory.getLogger(ArenaSelectionManager.class);

    private final GameManager gameManager;
    private final EventBus globalEventBus;

    private Map<String, ArenaManager> arenas = new HashMap<>();

    @Autowired
    public ArenaSelectionManager(GameManager gameManager, EventBus globalEventBus) {
        this.gameManager = gameManager;
        this.globalEventBus = globalEventBus;
    }

    public synchronized ArenaManager getArena(String arenaName) {
        if (StringUtils.isEmpty(arenaName)) {
            arenaName = OFFICIAL_ARENA_NAME;
        }

        ArenaManager ret = arenas.get(arenaName);

        if (ret == null) {

            ret = createNewArenaManager();
            ret.setArenaName(arenaName);
            if (arenaName.equals(OFFICIAL_ARENA_NAME)) {
                ret.setRanked(true);
            }
            arenas.put(arenaName, ret);
            log.info("Created new arena with name "+arenaName);
        }

        return ret;
    }

    private ArenaManager createNewArenaManager() {
        return new ArenaManager(gameManager, globalEventBus);
    }

    @Scheduled(fixedRate = 1000)
    public void runGameScheduler() {
        arenas.values().forEach(ArenaManager::runGameScheduler);
    }
}