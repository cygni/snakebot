package se.cygni.snake.api.util;

import org.junit.Test;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.response.HeartBeatResponse;

import static org.junit.Assert.assertEquals;

public class MessageUtilsTest {

    @Test
    public void extractGameId() throws Exception {
        GameStartingEvent gse = new GameStartingEvent("aaa", 2, 10, 10);

        assertEquals("aaa", MessageUtils.extractGameId(gse));
    }

    @Test
    public void extractGameIdDoesntExists() throws Exception {
        HeartBeatResponse hbr = new HeartBeatResponse();

        assertEquals(null, MessageUtils.extractGameId(hbr));
    }
}