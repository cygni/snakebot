package se.cygni.snake.arena;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ArenaSelectionManager {
    public static final String OFFICIAL_ARENA_NAME = "official";
    private static final Logger log = LoggerFactory.getLogger(ArenaSelectionManager.class);

    private Map<String, ArenaManager> arenas = new HashMap<>();

    @Autowired
    private ObjectFactory<ArenaManager> arenaManagerObjectFactory;

    public synchronized ArenaManager getArena(String arenaName) {
        ArenaManager ret = arenas.get(arenaName);

        if (StringUtils.isEmpty(arenaName)) {
            arenaName = OFFICIAL_ARENA_NAME;
        }

        if (ret == null) {

            ret = arenaManagerObjectFactory.getObject();
            ret.setArenaName(arenaName);
            if (arenaName.equals(OFFICIAL_ARENA_NAME)) {
                ret.setRanked(true);
            }
            arenas.put(arenaName, ret);
            log.info("Created new arena with name "+arenaName);
        }

        return ret;
    }
}