package se.cygni.snake.player.bot.anakonda;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ericwenn on 2017-11-08.
 */
public class Timer {

    private static Timer timer;
    private Map<String, Long> times = new HashMap<>();

    public Timer() {
    }

    public static String summary() {
        StringBuilder sb = new StringBuilder();
        for (String key : timer.times.keySet()) {

            Long sum = timer.times.get(key);

            sb.append(key + " took " + sum / 100000 + " \n");
        }
        return sb.toString();
    }

    public static void reset() {
        timer = new Timer();
    }

    public static void time(String key, long time) {
        if (timer.times.containsKey(key)) {
            Long l = timer.times.get(key);
            timer.times.put(key, l + time);
        } else {
            timer.times.put(key, time);
        }
    }

}
