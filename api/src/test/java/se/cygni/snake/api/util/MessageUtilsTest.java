package se.cygni.snake.api.util;

import org.junit.Test;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.response.HeartBeatResponse;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageUtilsTest {

    @Test
    public void extractGameId() throws Exception {
        GameStartingEvent gse = new GameStartingEvent("aaa", 2, 10, 10);

        Optional<String> value = MessageUtils.extractGameId(gse);
        assertTrue(value.isPresent());
        assertEquals("aaa", value.get());
    }

    @Test
    public void extractGameIdDoesntExists() throws Exception {
        HeartBeatResponse hbr = new HeartBeatResponse();

        Optional<String> value = MessageUtils.extractGameId(hbr);
        assertFalse(value.isPresent());
        assertEquals(Optional.empty(), value);
    }
}