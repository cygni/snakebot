package se.cygni.snake.eventapi.history;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.cygni.snake.api.GameMessage;
import se.cygni.snake.api.event.GameStartingEvent;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.eventapi.ApiMessageParser;

public class GameHistoryTest {

    @Test
    public void testSerialization() throws Exception {

	final String gameId = "game-has-id-3";
	final LocalDateTime now = LocalDateTime.now();
	final String[] players = new String[] { "Emil", "Johannes", "Barkis" };

	final List<GameMessage> gameMessages = new ArrayList<>();
	gameMessages.add(new GameStartingEvent(gameId, 3, 46, 34, new GameSettings()));

	final GameHistory gh = new GameHistory(gameId, players, now, gameMessages);

	final String msg = ApiMessageParser.encodeMessage(gh);
	System.out.println(msg);
	final GameHistory ghReparsed = (GameHistory) ApiMessageParser.decodeMessage(msg);

	assertEquals(gameId, ghReparsed.getGameId());
	assertArrayEquals(players, ghReparsed.getPlayerNames());
	final long secondsNow = now.toEpochSecond(ZoneOffset.UTC);
	final long gameHistorySeconds = ghReparsed.getGameDate().toEpochSecond(ZoneOffset.UTC);
	assertTrue(secondsNow == gameHistorySeconds);

	final GameStartingEvent gse = (GameStartingEvent) gh.getMessages().get(0);
	assertEquals(gameId, gse.getGameId());
	assertEquals(3, gse.getNoofPlayers());
	assertEquals(46, gse.getWidth());
	assertEquals(34, gse.getHeight());
    }
}