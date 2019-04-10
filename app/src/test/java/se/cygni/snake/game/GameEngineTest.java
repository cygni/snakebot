package se.cygni.snake.game;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

import se.cygni.game.Tile;
import se.cygni.game.TileMultipleContent;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.transformation.KeepOnlyObjectsOfType;
import se.cygni.game.transformation.KeepOnlySnakeWithId;
import se.cygni.game.transformation.MoveSnake;
import se.cygni.game.worldobject.Empty;
import se.cygni.game.worldobject.Food;
import se.cygni.game.worldobject.Obstacle;
import se.cygni.game.worldobject.SnakePart;
import se.cygni.game.worldobject.WorldObject;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.api.request.RegisterPlayer;

public class GameEngineTest {

    private Game game;

    private TileMultipleContent[] convertToTileMultipleContent(Tile[] tiles) {
	final TileMultipleContent[] mTiles = new TileMultipleContent[tiles.length];
	IntStream.range(0, tiles.length).forEach(pos -> {
	    final WorldObject wo = tiles[pos].getContent();
	    mTiles[pos] = new TileMultipleContent(wo);
	});
	return mTiles;
    }

    private Tile[] createWorldState(TileMultipleContent[] mTiles) {
	final Tile[] tiles = new Tile[mTiles.length];

	IntStream.range(0, mTiles.length).forEach(pos -> {
	    WorldObject wo = null;
	    if (mTiles[pos].hasContent()) {
		wo = mTiles[pos].getContents().get(0);
	    }
	    if (wo == null) {
		tiles[pos] = new Tile();
	    } else {
		tiles[pos] = new Tile(wo);
	    }
	});
	return tiles;
    }

    private TileMultipleContent[] mergeStates(List<WorldState> worldStates) {
	final WorldState firstState = worldStates.get(0);
	final TileMultipleContent[] mTiles = convertToTileMultipleContent(firstState.getTiles());

	for (int i = 1; i < worldStates.size(); i++) {
	    mergeTiles(mTiles, worldStates.get(i).getTiles());
	}

	return mTiles;
    }

    private TileMultipleContent[] mergeTiles(TileMultipleContent[] first, Tile[] second) {
	IntStream.range(0, first.length).forEach(pos -> {
	    System.out.println("merging pos: " + pos + " with " + second[pos].getContent());
	    final WorldObject contentSecond = second[pos].getContent();
	    first[pos].addContent(contentSecond);
	});
	return first;
    }

    @Before
    public void setup() {
	final GameFeatures gameFeatures = new GameFeatures();
	gameFeatures.setTimeInMsPerTick(1000);
	gameFeatures.setMaxNoofPlayers(25);
	gameFeatures.setSpontaneousGrowthEveryNWorldTick(2);
	gameFeatures.setTrainingGame(true);

	final GameManager gameManager = new GameManager(new EventBus());
	game = gameManager.createGame(gameFeatures);
    }

    @Test
    public void testGame() {
	game.registerPlayer(new RegisterPlayer("emil", new GameSettings()));
	game.registerPlayer(new RegisterPlayer("lisa", new GameSettings()));

	game.startGame();

    }

    @Test
    @Ignore
    public void testSimpleGame() {
	game.startGame();

	do {
	    try {
		Thread.sleep(10);
	    } catch (final Exception e) {
	    }
	} while (game.getGameEngine().isGameRunning());
    }

    @Test
    public void testStateMerge() throws Exception {
	WorldState ws = new WorldState(10, 10);

	final SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 22, 32);
	final SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 42, 43);

	ws = SnakeTestUtil.addSnake(ws, parts1);
	ws = SnakeTestUtil.addSnake(ws, parts2);

	final MoveSnake m1 = new MoveSnake(ws.getSnakeHeadForBodyAt(22), Direction.UP);
	final MoveSnake m2 = new MoveSnake(ws.getSnakeHeadForBodyAt(42), Direction.DOWN);
	final MoveSnake[] moves = { m1, m2 };

	final int noofSnakes = 2;

	@SuppressWarnings("unchecked")
	final KeepOnlyObjectsOfType worldBaseLine = new KeepOnlyObjectsOfType(
		new Class[] { Empty.class, Food.class, Obstacle.class });

	final List<WorldState> worldStates = new ArrayList<>();
	worldStates.add(worldBaseLine.transform(ws));

	for (int i = 0; i < noofSnakes; i++) {
	    final KeepOnlySnakeWithId removeOtherSnakes = new KeepOnlySnakeWithId("id" + (i + 1));
	    final WorldState oneSnakeWorld = removeOtherSnakes.transform(ws);
	    worldStates.add(moves[i].transform(oneSnakeWorld));
	}

	final TileMultipleContent[] mTiles = mergeStates(worldStates);
	final Tile[] tiles = createWorldState(mTiles);
	final WorldState newState = new WorldState(ws.getWidth(), ws.getHeight(), tiles);
	System.out.println("done");

	assertEquals(52, newState.getSnakeHeadForBodyAt(52).getPosition());
	assertArrayEquals(new int[] { 52, 42 }, newState.getSnakeSpread(newState.getSnakeHeadForBodyAt(52)));
	assertEquals(12, newState.getSnakeHeadForBodyAt(12).getPosition());
	assertArrayEquals(new int[] { 12, 22 }, newState.getSnakeSpread(newState.getSnakeHeadForBodyAt(12)));
    }
}
