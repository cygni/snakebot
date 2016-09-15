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
        gameFeatures.setHeadToTailConsumes(true);

        playerManager = mock(PlayerManager.class);
        game = mock(Game.class);
        when(game.getGlobalEventBus()).thenReturn(globalEventBus);
        when(game.getGameFeatures()).thenReturn(gameFeatures);
        when(game.getPlayerManager()).thenReturn(playerManager);
        when(game.getGameId()).thenReturn("mock-game-id");
    }


    @Test
    public void testSimpleMove() throws Exception {
        WorldState ws = new WorldState(10, 10);

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 22, 32);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 42, 43);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.UP);
                put(p2.getPlayerId(), Direction.DOWN);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(52, transformedWorld.getSnakeHeadForBodyAt(52).getPosition());
        assertEquals(p2.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(52).getPlayerId());
        assertArrayEquals(new int[] {52, 42}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(52)));

        assertEquals(12, transformedWorld.getSnakeHeadForBodyAt(12).getPosition());
        assertEquals(p1.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(12).getPlayerId());
        assertArrayEquals(new int[] {12, 22}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(12)));
    }


    @Test
    public void testCollisionWithWallMove() throws Exception {
        WorldState ws = new WorldState(10, 10);

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 2, 12);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 42, 43);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.UP);
                put(p2.getPlayerId(), Direction.DOWN);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(52, transformedWorld.getSnakeHeadForBodyAt(52).getPosition());
        assertArrayEquals(new int[] {52, 42}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(52)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);
        verify(p1).dead(5);
    }

    @Test
    public void testCollisionWithObstacleMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 15, 16);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 42, 43);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.LEFT);
                put(p2.getPlayerId(), Direction.DOWN);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(52, transformedWorld.getSnakeHeadForBodyAt(52).getPosition());
        assertArrayEquals(new int[] {52, 42}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(52)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).dead(5);
    }

    @Test
    public void testCollisionWithFoodMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();
        Food food = new Food();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 15, 16);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 42, 43);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 72);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, food, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.LEFT);
                put(p2.getPlayerId(), Direction.DOWN);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(52, transformedWorld.getSnakeHeadForBodyAt(52).getPosition());
        assertEquals(p2.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(52).getPlayerId());
        assertArrayEquals(new int[] {52, 42}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(52)));

        assertEquals(14, transformedWorld.getSnakeHeadForBodyAt(14).getPosition());
        assertEquals(p1.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(14).getPlayerId());
        assertArrayEquals(new int[] {14, 15, 16}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(14)));

        assertEquals(2, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(72).getContent());
        verify(p1).addPoints(PointReason.FOOD, gameFeatures.getPointsPerFood());
    }

    @Test
    public void testCollisionWithSnakePartMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 32, 33);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 42, 43, 44);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.DOWN);
                put(p2.getPlayerId(), Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(41, transformedWorld.getSnakeHeadForBodyAt(41).getPosition());
        assertEquals(p2.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(41).getPlayerId());
        assertArrayEquals(new int[] {41, 42, 43}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(41)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).dead(5);
        verify(p2).addPoints(PointReason.CAUSED_SNAKE_DEATH, gameFeatures.getPointsPerCausedDeath());
    }

    @Test
    public void testCollisionTwoSnakeHeadsMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");
        RemotePlayer p3 = createPlayer("player3");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 73, 72);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 75, 76);
        SnakePart[] parts3 = SnakeTestUtil.createSnake(p3.getName(), p3.getPlayerId(), 23, 24);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.addSnake(ws, parts3);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.RIGHT);
                put(p2.getPlayerId(), Direction.LEFT);
                put(p3.getPlayerId(), Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(22, transformedWorld.getSnakeHeadForBodyAt(22).getPosition());
        assertEquals(p3.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(22).getPlayerId());
        assertArrayEquals(new int[] {22, 23}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(22)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).dead(5);
        verify(p2).dead(5);
    }

    @Test
    public void testCollisionHeadToTailMove() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");
        RemotePlayer p3 = createPlayer("player3");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 34, 35);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 33, 43);
        SnakePart[] parts3 = SnakeTestUtil.createSnake(p3.getName(), p3.getPlayerId(), 82, 81);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.addSnake(ws, parts3);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.LEFT);
                put(p2.getPlayerId(), Direction.UP);
                put(p3.getPlayerId(), Direction.RIGHT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(33, transformedWorld.getSnakeHeadForBodyAt(33).getPosition());
        assertEquals(p1.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(33).getPlayerId());
        assertArrayEquals(new int[] {33,34}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(33)));

        assertEquals(23, transformedWorld.getSnakeHeadForBodyAt(23).getPosition());
        assertEquals(p2.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(23).getPlayerId());
        assertArrayEquals(new int[] {23}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(23)));

        assertEquals(83, transformedWorld.getSnakeHeadForBodyAt(83).getPosition());
        assertEquals(p3.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(83).getPlayerId());
        assertArrayEquals(new int[] {83, 82}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(83)));

        assertEquals(3, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).addPoints(PointReason.NIBBLE, gameFeatures.getPointsPerNibble());
    }

    @Test
    public void testCollisionTwoSnakeHeadsPassingMove() throws Exception {
        gameFeatures.setHeadToTailConsumes(false);
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");
        RemotePlayer p3 = createPlayer("player3");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 34, 33);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 35, 36);
        SnakePart[] parts3 = SnakeTestUtil.createSnake(p3.getName(), p3.getPlayerId(), 23, 24);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.addSnake(ws, parts3);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.RIGHT);
                put(p2.getPlayerId(), Direction.LEFT);
                put(p3.getPlayerId(), Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(22, transformedWorld.getSnakeHeadForBodyAt(22).getPosition());
        assertEquals(p3.getPlayerId(), transformedWorld.getSnakeHeadForBodyAt(22).getPlayerId());
        assertArrayEquals(new int[] {22, 23}, transformedWorld.getSnakeSpread(transformedWorld.getSnakeHeadForBodyAt(22)));

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).dead(5);
        verify(p2).dead(5);
    }

    @Test
    public void testCollisionThreeHeads() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");
        RemotePlayer p3 = createPlayer("player3");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 34, 33);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 36, 37);
        SnakePart[] parts3 = SnakeTestUtil.createSnake(p3.getName(), p3.getPlayerId(), 45, 55);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.addSnake(ws, parts3);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.RIGHT);
                put(p2.getPlayerId(), Direction.LEFT);
                put(p3.getPlayerId(), Direction.UP);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(0, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).dead(5);
        verify(p2).dead(5);
        verify(p3).dead(5);
    }

    @Test
    public void testCollisionHeadToHead() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 34, 33);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 36, 37);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.RIGHT);
                put(p2.getPlayerId(), Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(0, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).dead(5);
        verify(p2).dead(5);
    }

    @Test
    public void testCollisionPassThrough() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 34, 33, 32);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 35, 36, 37);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.RIGHT);
                put(p2.getPlayerId(), Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(0, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).dead(5);
        verify(p2).dead(5);
    }

    @Test
    public void testCollisionTwoSnakeHeadsPassingLandingOnTail() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 34, 33);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 35, 36);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.RIGHT);
                put(p2.getPlayerId(), Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(0, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).dead(5);
        verify(p2).dead(5);
    }

    @Test
    public void testEatEachOthersTail() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 33, 43, 53);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 44, 34, 24);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.RIGHT);
                put(p2.getPlayerId(), Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(2, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);

        SnakeHead head1 = transformedWorld.getSnakeHeadById(p1.getPlayerId());
        assertEquals(2, transformedWorld.getSnakeSpread(head1).length);

        SnakeHead head2 = transformedWorld.getSnakeHeadById(p2.getPlayerId());
        assertEquals(2, transformedWorld.getSnakeSpread(head2).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p1).addPoints(
                PointReason.NIBBLE,
                gameFeatures.getPointsPerNibble()
        );
        verify(p2).addPoints(
                PointReason.NIBBLE,
                gameFeatures.getPointsPerNibble()
        );
    }

    @Test
    public void testOneEatsTailOtherCollides() throws Exception {
        WorldState ws = new WorldState(10, 10);
        Obstacle obstacle = new Obstacle();

        RemotePlayer p1 = createPlayer("player1");
        RemotePlayer p2 = createPlayer("player2");

        SnakePart[] parts1 = SnakeTestUtil.createSnake(p1.getName(), p1.getPlayerId(), 63, 73, 83, 93);
        SnakePart[] parts2 = SnakeTestUtil.createSnake(p2.getName(), p2.getPlayerId(), 74, 64, 54);

        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);
        ws = SnakeTestUtil.replaceWorldObjectAt(ws, obstacle, 14);

        Map<String, Direction> snakeDirections = new HashMap<String, Direction>() {
            {
                put(p1.getPlayerId(), Direction.RIGHT);
                put(p2.getPlayerId(), Direction.LEFT);
            }
        };

        WorldTransformer transformer = new WorldTransformer(game.getGameFeatures(), game.getPlayerManager(), game.getGameId(), game.getGlobalEventBus());
        WorldState transformedWorld = transformer.transform(snakeDirections, gameFeatures, ws, false, 5);

        assertEquals(1, transformedWorld.listPositionsWithContentOf(SnakeHead.class).length);
        SnakeHead head1 = transformedWorld.getSnakeHeadById(p1.getPlayerId());
        assertEquals(4, transformedWorld.getSnakeSpread(head1).length);

        assertEquals(obstacle, transformedWorld.getTile(14).getContent());
        verify(p2).dead(5);
        verify(p1).addPoints(
                PointReason.NIBBLE,
                gameFeatures.getPointsPerNibble()
        );
    }

    private RemotePlayer createPlayer(String name) {
        RemotePlayer mockPlayer = mock(RemotePlayer.class);
        when(mockPlayer.getName()).thenReturn(name);
        when(mockPlayer.getPlayerId()).thenReturn("id-" + name);

        when(playerManager.getPlayer("id-" + name)).thenReturn(mockPlayer);

        return mockPlayer;
    }
}
