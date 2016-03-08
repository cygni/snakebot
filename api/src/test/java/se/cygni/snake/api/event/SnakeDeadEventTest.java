package se.cygni.snake.api.event;

import org.junit.Test;
import se.cygni.snake.api.GameMessageParser;
import se.cygni.snake.api.model.DeathReason;
import static org.junit.Assert.*;

public class SnakeDeadEventTest {

    @Test
    public void testSerializationSnakeDeadEvent() throws Exception {
        SnakeDeadEvent sde = new SnakeDeadEvent(DeathReason.CollisionWithWall, "playerId", 5, 10, "6666", 99);
        TestUtil.populateBaseData(sde, "rPlayerId");

        String serialized = GameMessageParser.encodeMessage(sde);

        SnakeDeadEvent parsedSde = (SnakeDeadEvent)GameMessageParser.decodeMessage(serialized);

        assertEquals(DeathReason.CollisionWithWall, parsedSde.getDeathReason());
        assertEquals("playerId", parsedSde.getPlayerId());
        assertEquals(5, parsedSde.getX());
        assertEquals(10, parsedSde.getY());
        assertEquals("6666", parsedSde.getGameId());
        assertEquals(99, parsedSde.getGameTick());
        assertEquals("rPlayerId", parsedSde.getReceivingPlayerId());
    }
}