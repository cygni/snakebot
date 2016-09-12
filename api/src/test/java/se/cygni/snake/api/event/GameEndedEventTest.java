package se.cygni.snake.api.event;

import org.junit.Test;
import se.cygni.snake.api.GameMessageParser;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class GameEndedEventTest {

    @Test
    public void testSerializationGameEndedEvent() throws Exception {
        GameEndedEvent gee = new GameEndedEvent("playerWinnerId", "666", 4, null);
        TestUtil.populateBaseData(gee, "rPlayerId");

        String serialized = GameMessageParser.encodeMessage(gee);
        GameEndedEvent parsedgee = (GameEndedEvent)GameMessageParser.decodeMessage(serialized);

        assertEquals("playerWinnerId", parsedgee.getPlayerWinnerId());
        assertEquals("666", parsedgee.getGameId());
        assertEquals(4, parsedgee.getGameTick());
        assertEquals("rPlayerId", parsedgee.getReceivingPlayerId());
    }

    @Test
    public void testSomething() throws Exception {
        GameLinkEvent gle = new GameLinkEvent("d1388638-2fcb-488b-893a-672a1182315c", "http://localhost:8090/#/viewgame?gameid=d1388638-2fcb-488b-893a-672a1182315c");
        gle.setReceivingPlayerId(UUID.randomUUID().toString());

        String serialized = GameMessageParser.encodeMessage(gle);
        System.out.println(serialized);
    }
}