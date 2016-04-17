package se.cygni.snake.game;

import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.worldobject.Food;
import se.cygni.game.worldobject.Obstacle;
import se.cygni.game.worldobject.SnakeHead;
import se.cygni.game.worldobject.SnakePart;
import se.cygni.snake.api.model.PointReason;
import se.cygni.snake.player.RemotePlayer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class WorldTransformerTest {

    Game game;
    PlayerManager playerManager;
    EventBus globalEventBus;
    GameFeatures gameFeatures;

    @Before
    public void setup() {
        globalEventBus = new EventBus("globaleventbus");
        gameFeatures = new GameFeatures();

        playerManager = mock(PlayerManager.class);
        game = mock(Game.class);
        when(game.getGlobalEventBus()).thenReturn(globalEventBus);
        when(game.getGameFeatures()).thenReturn(gameFeatures);
        when(game.getPlayerManager()).thenReturn(playerManager);
    }


    @Test
    public void testSimpleMove() throws Exception {
        WorldState ws = new WorldState(10, 10);

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 22, 32);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 42, 43);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put("id1", Direction.UP);
                put("id2", Direction.DOWN);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(52, transformedWorld.getSnakeHeadForBodyAt(52).getPosition());
        assertEquals("id2", transformedWorld.getSnakeHeadForBodyAt(52).getPlayerId());
        assertArrayEquals(new int[] {52, 42}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(52)));

        assertEquals(12, transformedWorld.getSnakeHeadForBodyAt(12).getPosition());
        assertEquals("id1", transformedWorld.getSnakeHeadForBodyAt(12).getPlayerId());
        assertArrayEquals(new int[] {12, 22}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(12)));
    }


    @Test
    public void testCollisionWithWallMove() throws Exception {
        WorldState ws = new WorldState(10, 10);

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 2, 12);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 42, 43);

        RemotePlayer mockedPlayer1 = mock(RemotePlayer.class);
        when(playerManager.getPlayer("id1")).thenReturn(mockedPlayer1);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put("id1", Direction.UP);
                put("id2", Direction.DOWN);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(52, transformedWorld.getSnakeHeadForBodyAt(52).getPosition());
        assertArrayEquals(new int[] {52, 42}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(52)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);
        verify(mockedPlayer1).dead();
    }

    @Test
    public void testCollisionWithObstacleMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 15, 16);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 42, 43);

        RemotePlayer mockedPlayer1 = mock(RemotePlayer.class);
        when(playerManager.getPlayer("id1")).thenReturn(mockedPlayer1);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put("id1", Direction.LEFT);
                put("id2", Direction.DOWN);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(52, transformedWorld.getSnakeHeadForBodyAt(52).getPosition());
        assertArrayEquals(new int[] {52, 42}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(52)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(mockedPlayer1).dead();
    }

    @Test
    public void testCollisionWithFoodMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();
        Food food = new Food();

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 15, 16);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 42, 43);

        RemotePlayer mockedPlayer1 = mock(RemotePlayer.class);
        when(playerManager.getPlayer("id1")).thenReturn(mockedPlayer1);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 72);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, food, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put("id1", Direction.LEFT);
                put("id2", Direction.DOWN);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(52, transformedWorld.getSnakeHeadForBodyAt(52).getPosition());
        assertEquals("id2", transformedWorld.getSnakeHeadForBodyAt(52).getPlayerId());
        assertArrayEquals(new int[] {52, 42}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(52)));

        assertEquals(14, transformedWorld.getSnakeHeadForBodyAt(14).getPosition());
        assertEquals("id1", transformedWorld.getSnakeHeadForBodyAt(14).getPlayerId());
        assertArrayEquals(new int[] {14, 15, 16}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(14)));

        assertEquals(2, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(72).getContent());
        verify(mockedPlayer1).addPoints(PointReason.FOOD, gameFeatures.getPointsPerFood());
    }

    @Test
    public void testCollisionWithSnakePartMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 32, 33);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 42, 43, 44);

        RemotePlayer mockedPlayer1 = mock(RemotePlayer.class);
        RemotePlayer mockedPlayer2 = mock(RemotePlayer.class);
        when(playerManager.getPlayer("id1")).thenReturn(mockedPlayer1);
        when(playerManager.getPlayer("id2")).thenReturn(mockedPlayer2);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put("id1", Direction.DOWN);
                put("id2", Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(41, transformedWorld.getSnakeHeadForBodyAt(41).getPosition());
        assertEquals("id2", transformedWorld.getSnakeHeadForBodyAt(41).getPlayerId());
        assertArrayEquals(new int[] {41, 42, 43}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(41)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(mockedPlayer1).dead();
        verify(mockedPlayer2).addPoints(PointReason.CAUSED_SNAKE_DEATH, gameFeatures.getPointsPerCausedDeath());
    }

    @Test
    public void testCollisionTwoSnakeHeadsMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 73, 72);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 75, 76);
        SnakePart[] parts3 = SnakeTestUtil.createSnake("test3", "id3", 23, 24);

        RemotePlayer mockedPlayer1 = mock(RemotePlayer.class);
        RemotePlayer mockedPlayer2 = mock(RemotePlayer.class);
        when(playerManager.getPlayer("id1")).thenReturn(mockedPlayer1);
        when(playerManager.getPlayer("id2")).thenReturn(mockedPlayer2);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.addSnake(ws, parts3);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put("id1", Direction.RIGHT);
                put("id2", Direction.LEFT);
                put("id3", Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(22, transformedWorld.getSnakeHeadForBodyAt(22).getPosition());
        assertEquals("id3", transformedWorld.getSnakeHeadForBodyAt(22).getPlayerId());
        assertArrayEquals(new int[] {22, 23}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(22)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(mockedPlayer1).dead();
        verify(mockedPlayer2).dead();
    }

    @Test
    public void testCollisionHeadToTailMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 34, 35);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 33, 43);
        SnakePart[] parts3 = SnakeTestUtil.createSnake("test3", "id3", 82, 81);

        RemotePlayer mockedPlayer1 = mock(RemotePlayer.class);
        RemotePlayer mockedPlayer2 = mock(RemotePlayer.class);
        when(playerManager.getPlayer("id1")).thenReturn(mockedPlayer1);
        when(playerManager.getPlayer("id2")).thenReturn(mockedPlayer2);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.addSnake(ws, parts3);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put("id1", Direction.LEFT);
                put("id2", Direction.UP);
                put("id3", Direction.RIGHT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(33, transformedWorld.getSnakeHeadForBodyAt(33).getPosition());
        assertEquals("id1", transformedWorld.getSnakeHeadForBodyAt(33).getPlayerId());
        assertArrayEquals(new int[] {33,34}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(33)));

        assertEquals(23, transformedWorld.getSnakeHeadForBodyAt(23).getPosition());
        assertEquals("id2", transformedWorld.getSnakeHeadForBodyAt(23).getPlayerId());
        assertArrayEquals(new int[] {23}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(23)));

        assertEquals(83, transformedWorld.getSnakeHeadForBodyAt(83).getPosition());
        assertEquals("id3", transformedWorld.getSnakeHeadForBodyAt(83).getPlayerId());
        assertArrayEquals(new int[] {83, 82}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(83)));

        assertEquals(3, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(mockedPlayer1).addPoints(PointReason.NIBBLE, gameFeatures.getPointsPerNibble());
    }

    @Test
    public void testCollisionTwoSnakeHeadsPassingMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 34, 33);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 35, 36);
        SnakePart[] parts3 = SnakeTestUtil.createSnake("test3", "id3", 23, 24);

        RemotePlayer mockedPlayer1 = mock(RemotePlayer.class);
        RemotePlayer mockedPlayer2 = mock(RemotePlayer.class);
        when(playerManager.getPlayer("id1")).thenReturn(mockedPlayer1);
        when(playerManager.getPlayer("id2")).thenReturn(mockedPlayer2);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.addSnake(ws, parts3);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put("id1", Direction.RIGHT);
                put("id2", Direction.LEFT);
                put("id3", Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(22, transformedWorld.getSnakeHeadForBodyAt(22).getPosition());
        assertEquals("id3", transformedWorld.getSnakeHeadForBodyAt(22).getPlayerId());
        assertArrayEquals(new int[] {22, 23}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(22)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(mockedPlayer1).dead();
        verify(mockedPlayer2).dead();
    }

    @Test
    public void testCollisionTwoSnakeHeadsPassingAllDeadMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 34, 33);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 35, 36);

        RemotePlayer mockedPlayer1 = mock(RemotePlayer.class);
        RemotePlayer mockedPlayer2 = mock(RemotePlayer.class);
        when(playerManager.getPlayer("id1")).thenReturn(mockedPlayer1);
        when(playerManager.getPlayer("id2")).thenReturn(mockedPlayer2);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put("id1", Direction.RIGHT);
                put("id2", Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(0, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(mockedPlayer1).dead();
        verify(mockedPlayer2).dead();
    }
}
