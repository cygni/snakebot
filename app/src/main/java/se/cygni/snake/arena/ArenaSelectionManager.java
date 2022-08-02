package se.cygni.snake.arena;


import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.cygni.snake.api.event.ArenaEndedEvent;
import se.cygni.snake.event.InternalGameEvent;
import se.cygni.snake.game.GameManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private String generateRandomArenaName() {
        String characters = "abcdefghijklmnopqrstuvwxyz".toUpperCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(characters.charAt((int) (Math.random() * characters.length())));
        }
        return sb.toString();
    }

    public synchronized ArenaManager createArena() {
        // Randomize arena name
        String arenaName = generateRandomArenaName();

        // Regenerate arena name if it already exists
        while (arenas.containsKey(arenaName)) {
            arenaName = generateRandomArenaName();
        }

        // Create arena
        ArenaManager arena = createNewArenaManager();
        arena.setArenaName(arenaName);
        arenas.put(arenaName, arena);
        log.info("Created new arena {}", arenaName);
        return arena;
    }

    public synchronized ArenaManager getArena(String arenaName) {
        // if (StringUtils.isEmpty(arenaName)) {
        //     arenaName = OFFICIAL_ARENA_NAME;
        // }

        ArenaManager ret = arenas.get(arenaName);

        // if (ret == null) {

        //     ret = createNewArenaManager();
        //     ret.setArenaName(arenaName);
        //     if (arenaName.equals(OFFICIAL_ARENA_NAME)) {
        //         ret.setRanked(true);
        //     }
        //     arenas.put(arenaName, ret);
        //     log.info("Created new arena with name "+arenaName);
        // }

        return ret;
    }

    public synchronized void removeArena(String arenaName) {
        arenas.remove(arenaName);
    }

    private ArenaManager createNewArenaManager() {
        return new ArenaManager(gameManager, globalEventBus);
    }

    @Scheduled(fixedRate = 1000)
    public void runGameScheduler() {
        System.out.println("Arenas: "+arenas.size());

        List<ArenaManager> inActiveArenas = new ArrayList<ArenaManager>();
        for (ArenaManager arena : arenas.values()) {
            if (!arena.isActive()) inActiveArenas.add(arena);
            arena.runGameScheduler();
        }
        // Remove inactive arenas
        for (ArenaManager arena : inActiveArenas) {
            String arenaName = arena.getArenaName();
            log.info("Arena {} is inactive, removing it", arenaName);
            globalEventBus.post(new InternalGameEvent(System.currentTimeMillis(), new ArenaEndedEvent(arenaName)));
            arenas.remove(arenaName);
        }
    }
}