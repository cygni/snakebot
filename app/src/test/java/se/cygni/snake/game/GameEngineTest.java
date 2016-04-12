package se.cygni.snake.game;

import com.google.common.eventbus.EventBus;
import org.junit.Ignore;
import org.junit.Test;
import se.cygni.game.Tile;
import se.cygni.game.TileMultipleContent;
import se.cygni.game.WorldState;
import se.cygni.game.enums.Direction;
import se.cygni.game.testutil.SnakeTestUtil;
import se.cygni.game.transformation.KeepOnlyObjectsOfType;
import se.cygni.game.transformation.KeepOnlySnakeWithId;
import se.cygni.game.transformation.MoveSnake;
import se.cygni.game.worldobject.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class GameEngineTest {

    private GameEngine gameEngine;
    private Game game;

    //@Before
    public void setup() {
        GameFeatures gameFeatures = new GameFeatures();
        gameFeatures.setWidth(15);
        gameFeatures.setHeight(15);
        gameFeatures.setTimeInMsPerTick(1000);
        gameFeatures.setMaxNoofPlayers(25);
        gameFeatures.setSpontaneousGrowthEveryNWorldTick(2);
        gameFeatures.setTrainingGame(true);

        GameManager gameManager = new GameManager(new EventBus());
        game = gameManager.createGame(gameFeatures);

        gameEngine = game.getGameEngine();
    }

    @Test @Ignore
    public void testSimpleGame() {
        game.startGame();

        do {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        } while (game.getGameEngine().isGameRunning());
    }

    @Test
    public void testStateMerge() throws Exception {
        WorldState ws = new WorldState(10, 10);

        SnakePart[] parts1 = SnakeTestUtil.createSnake("test1", "id1", 22, 32);
        SnakePart[] parts2 = SnakeTestUtil.createSnake("test2", "id2", 42, 43);


        ws = SnakeTestUtil.addSnake(ws, parts1);
        ws = SnakeTestUtil.addSnake(ws, parts2);

        MoveSnake m1 = new MoveSnake(ws.getSnakeHeadForBodyAt(22), Direction.UP);
        MoveSnake m2 = new MoveSnake(ws.getSnakeHeadForBodyAt(42), Direction.DOWN);
        MoveSnake[] moves = {m1, m2};

        int noofSnakes = 2;

        KeepOnlyObjectsOfType worldBaseLine = new KeepOnlyObjectsOfType(new Class[] {Empty.class, Food.class, Obstacle.class});

        List<WorldState> worldStates = new ArrayList<>();
        worldStates.add(worldBaseLine.transform(ws));

        for (int i = 0; i < noofSnakes; i++) {
            KeepOnlySnakeWithId removeOtherSnakes = new KeepOnlySnakeWithId("id" + (i+1));
            WorldState oneSnakeWorld = removeOtherSnakes.transform(ws);
            worldStates.add(moves[i].transform(oneSnakeWorld));
        }

        TileMultipleContent[] mTiles = mergeStates(worldStates);
        Tile[] tiles = createWorldState(mTiles);
        WorldState newState = new WorldState(ws.getWidth(), ws.getHeight(), tiles);
        System.out.println("done");

        assertEquals(52, newState.getSnakeHeadForBodyAt(52).getPosition());
        assertArrayEquals(new int[] {52, 42}, newState.getSnakeSpread(newState.getSnakeHeadForBodyAt(52)));
        assertEquals(12, newState.getSnakeHeadForBodyAt(12).getPosition());
        assertArrayEquals(new int[] {12, 22}, newState.getSnakeSpread(newState.getSnakeHeadForBodyAt(12)));
    }


    private Tile[] createWorldState(TileMultipleContent[] mTiles) {
        Tile[] tiles = new Tile[mTiles.length];

        IntStream.range(0, mTiles.length).forEach(
                pos -> {
                    WorldObject wo = null;
                    if (mTiles[pos].hasContent())
                        wo = mTiles[pos].getContents().get(0);
                    if (wo == null)
                        tiles[pos] = new Tile();
                    else
                        tiles[pos] = new Tile(wo);
                }
        );
        return tiles;
    }

    private TileMultipleContent[] mergeStates(List<WorldState> worldStates) {
        WorldState firstState = worldStates.get(0);
        TileMultipleContent[] mTiles = convertToTileMultipleContent(
                firstState.getTiles());

        for (int i = 1; i < worldStates.size(); i++) {
            mergeTiles(mTiles, worldStates.get(i).getTiles());
        }

        return mTiles;
    }

    private TileMultipleContent[] convertToTileMultipleContent(Tile[] tiles) {
        TileMultipleContent[] mTiles = new TileMultipleContent[tiles.length];
        IntStream.range(0, tiles.length).forEach(
                pos -> {
                    WorldObject wo = tiles[pos].getContent();
                    mTiles[pos] = new TileMultipleContent(wo);
                }
        );
        return mTiles;
    }

    private TileMultipleContent[] mergeTiles(TileMultipleContent[] first, Tile[] second) {
        IntStream.range(0, first.length).forEach(
                pos -> {
                    System.out.println("merging pos: " + pos + " with "+second[pos].getContent());
                    WorldObject contentSecond = second[pos].getContent();
                    first[pos].addContent(contentSecond);
                }
        );
        return first;
    }
}
